import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.lang.annotation.Annotation
import java.lang.reflect.Method

abstract class GeneratePinSchemasTask extends DefaultTask {

    @InputFiles
    abstract ConfigurableFileCollection getClasspath()

    @OutputDirectory
    abstract DirectoryProperty getOutputDir()

    @TaskAction
    void generate() {
        File out = outputDir.get().asFile
        out.mkdirs()

        Map<String, Map<String, Map<String, String>>> contexts = scanContexts()
        writePinTypeSchema(out, contexts)
        writePinStateSchema(out)

        logger.lifecycle("Generated pin schemas: ${contexts.keySet()}")
    }

    private Map<String, Map<String, Map<String, String>>> scanContexts() {
        Map<String, Map<String, Map<String, String>>> result = [:]

        URL[] urls = classpath.collect { it.toURI().toURL() } as URL[]
        URLClassLoader loader = new URLClassLoader(urls, (ClassLoader) null)

        Class<? extends Annotation> pinContextClass = (Class<? extends Annotation>) Class.forName(
            'net.sircesarium.cartography.data.pin.context.PinContext', true, loader
        )
        Class<? extends Annotation> pinGetterClass = (Class<? extends Annotation>) Class.forName(
            'net.sircesarium.cartography.data.pin.context.PinGetter', true, loader
        )

        classpath.each { File root ->
            if (!root.isDirectory()) return

            root.eachDirRecurse { File dir ->
                dir.listFiles()?.each { File file ->
                    if (!file.name.endsWith('.class')) return

                    String rel = file.absolutePath.substring(root.absolutePath.length() + 1)
                    String fqcn = rel.replace('\\', '/').replace('/', '.').replace('.class', '')

                    try {
                        Class<?> clazz = Class.forName(fqcn, false, loader)
                        def ctx = clazz.getAnnotation(pinContextClass)
                        if (ctx == null) return

                        String contextName = ctx.value()
                        Map<String, Map<String, String>> getters = [:]

                        clazz.methods.each { Method method ->
                            def g = method.getAnnotation(pinGetterClass)
                            if (g != null) {
                                getters[(String) g.value()] = [type: (String) g.type(), desc: (String) g.description()]
                            }
                        }

                        result[(String) contextName] = getters
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return result
    }

    private static void writePinTypeSchema(File out, Map<String, Map<String, Map<String, String>>> contexts) {
        List<Map> rules = []

        rules << [
            'if':   [properties: [type: [const: 'dummy']]],
            'then': [
                'not': [required: ['data']],
                additionalProperties: false
            ]
        ]

        contexts.each { String name, Map<String, Map<String, String>> getters ->
            List<String> validGetters = getters.keySet().toList()

            rules << [
                'if':   [properties: [type: [const: name]]],
                'then': [properties: [data: [type: 'array', items: [type: 'string', enum: validGetters], uniqueItems: true]]]
            ]
        }

        Map<String, Object> schema = [
            '\$schema'  : 'https://json-schema.org/draft/2020-12/schema',
            title       : 'Cartography Pin Type',
            description : 'Defines a pin type. Loaded from datapacks.',
            type        : 'object',
            required    : ['type'],
            properties  : [
                type     : [type: 'string', enum: contexts.keySet().toList() + ['dummy']],
                lifetime : [type: 'integer', description: 'Lifetime in seconds', default: 300, minimum: 1],
                data     : [type: 'array', description: 'Getter names to resolve from context']
            ],
            allOf       : rules
        ]

        new File(out, 'pin_type_schema.json').text = JsonOutput.prettyPrint(JsonOutput.toJson(schema))
    }

    private static void writePinStateSchema(File out) {
        Map<String, Object> stateProps = [
            condition : [type: 'string', description: 'Template condition for this state'],
            color     : [type: 'string', description: 'Hex color for the pin marker'],
            icon      : [type: 'string', description: 'Registry name of the icon'],
            mapIcon   : [type: 'string', description: 'Registry name of the map icon'],
            name      : [type: 'string', description: 'Display name override']
        ]

        Map<String, Object> single = [
            type: 'object',
            properties: stateProps,
            required: ['color', 'icon']
        ]

        Map<String, Object> schema = [
            '\$schema'  : 'https://json-schema.org/draft/2020-12/schema',
            title       : 'Cartography Pin State',
            description : 'Visual state for a pin type. Loaded from resource packs.',
            oneOf       : [
                single,
                [type: 'array', items: single, minItems: 1, description: 'Multiple conditional states']
            ]
        ]

        new File(out, 'pin_state_schema.json').text = JsonOutput.prettyPrint(JsonOutput.toJson(schema))
    }
}

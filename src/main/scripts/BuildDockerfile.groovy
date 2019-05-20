Properties props = new Properties()
File propsFile = new File("${project.basedir}/src/main/resources/application.properties")
props.load(propsFile.newDataInputStream())

String template = new File("${project.basedir}/src/main/resources/DockerfileTemplate".toString()).getText()

def dockerFileText = new groovy.text.SimpleTemplateEngine().createTemplate(template)
        .make([
                fileName: project.build.finalName,
                workdir: props.getProperty("box.container.home")
        ])

println "writing dir " + "${project.basedir}/target/dockerfile"
new File("${project.basedir}/target/dockerfile/".toString()).mkdirs()

println "writing file"
File dockerFile = new File("${project.basedir}/target/dockerfile/Dockerfile".toString())

dockerFile.withWriter('UTF-8') { writer ->
    writer.write(dockerFileText)
}
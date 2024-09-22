package org.team11260.tasks.konfig

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class KonfigPullFiles : DefaultTask() {

    @InputDirectory
    abstract fun getOutputDir(): DirectoryProperty

    @Input
    abstract fun getBundleBaseName(): Property<String>

    @InputFile
    abstract fun getAdbExecutable(): RegularFileProperty

    @Input
    abstract fun getDeployLocation(): Property<String>

    @TaskAction
    fun execute() {
        val autonomous = File(project.projectDir, "konfig")
        if (!autonomous.exists()) {
            error("The autonomous file does not exist.")
        }

        val files = autonomous.listFiles() ?: arrayOf()
        for (file in files)
        {
            file.delete()
        }

        project.exec {
            it.commandLine(
                getAdbExecutable().get(),
                "pull",
                getDeployLocation().get() + "settings/",
                autonomous.absolutePath
            )
        }
    }
}

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

abstract class KonfigPushFiles : DefaultTask() {

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
        if (files.isEmpty())
        {
            project.logger.info("No autonomous updated")
            return
        }

        files.forEach { file ->
            project.exec {
                it.commandLine(
                    getAdbExecutable().get(),
                    "push",
                    file.absolutePath,
                    getDeployLocation().get() + "settings/",
                )
            }

        }
    }
}

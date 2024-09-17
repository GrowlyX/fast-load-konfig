package org.team11260.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecException
import java.io.File

/**
 * Uses ADB to copy the merged dex jar to the robot controller.
 */
abstract class AutonomousUpdate : DefaultTask() {

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
        val autonomous = File(project.projectDir, "autonomous")
        if (!autonomous.exists()) {
            error("The autonomous file does not exist.")
        }

        val files = autonomous.listFiles() ?: arrayOf()
        if (files.isEmpty())
        {
            project.logger.info("No autonomous updated")
            return
        }

        try {

            project.exec {
                files.forEach { file ->
                    it.commandLine(
                        getAdbExecutable().get(),
                        "push",
                        file.absolutePath + "/settings/",
                        getDeployLocation().get(),
                    )
                }

            }
        } catch (e: ExecException) {
            error("Failed to connect to robot, ensure ADB connected to robot.")
        }
    }
}

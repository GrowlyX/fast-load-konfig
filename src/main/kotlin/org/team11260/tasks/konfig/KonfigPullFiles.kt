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

abstract class KonfigPullFiles : DefaultTask()
{

    @InputDirectory
    abstract fun getOutputDir(): DirectoryProperty

    @Input
    abstract fun getBundleBaseName(): Property<String>

    @InputFile
    abstract fun getAdbExecutable(): RegularFileProperty

    @Input
    abstract fun getDeployLocation(): Property<String>

    @TaskAction
    fun execute()
    {
        val autonomous = File(project.projectDir, "konfig")
        if (!autonomous.exists())
        {
            autonomous.mkdirs()
        }

        val files = autonomous.listFiles() ?: arrayOf()
        for (file in files)
        {
            file.delete()
        }

        val primerDirectory = File(project.buildDir, "primer")
        if (primerDirectory.exists())
        {
            primerDirectory.deleteRecursively()
        }

        primerDirectory.mkdirs()

        project.exec {
            it.commandLine(
                getAdbExecutable().get(),
                "pull",
                getDeployLocation().get() + "settings/",
                primerDirectory.absolutePath
            )
        }

        val settingsDirectory = File(primerDirectory, "settings")
        settingsDirectory.listFiles()?.forEach {
            val newFile = File(autonomous, it.name)
            if (!it.exists())
            {
                it.createNewFile()
            }

            newFile.writeBytes(it.readBytes())
        }
    }
}

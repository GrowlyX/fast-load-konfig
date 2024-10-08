package org.team11260

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.team11260.tasks.*
import org.team11260.tasks.konfig.KonfigPullFiles
import org.team11260.tasks.konfig.KonfigPushFiles

const val DEX_BASE_NAME_CONVENTION = "FastLoadDex"
const val BUNDLE_BASE_NAME_CONVENTION = "FastLoadBundle"
const val DEPLOY_LOCATION_CONVENTION = "/storage/emulated/0/FIRST/"

/**
 * Main plugin class to add all the tasks to the Gradle registry and manage task dependencies.
 */
class FastLoadPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        val extension = project.extensions.create("fastLoad", FastLoadExtension::class.java)

        extension.getAdbExecutable().convention(androidComponents.sdkComponents.adb)
        extension.getOutputDir().convention(project.layout.buildDirectory.dir("libs"))
        extension.getDexBaseName().convention(DEX_BASE_NAME_CONVENTION)
        extension.getBundleBaseName().convention(BUNDLE_BASE_NAME_CONVENTION)
        extension.getDeployLocation().convention(DEPLOY_LOCATION_CONVENTION)

        val dexFastLoad = project.tasks.register("fastLoadClasses", DexFastLoad::class.java) { task ->
            task.group = "build"

            task.getOutputDir().convention(extension.getOutputDir())
            task.getDexBaseName().convention(extension.getDexBaseName())

            task.dependsOn("dexBuilderDebug")
        }
        val assembleFastLoad = project.tasks.register("assembleFastLoad", AssembleFastLoad::class.java) { task ->
            task.group = "build"

            task.getOutputDir().convention(extension.getOutputDir())
            task.getDexBaseName().convention(extension.getDexBaseName())
            task.getBundleBaseName().convention(extension.getBundleBaseName())

            task.dependsOn(dexFastLoad)
        }
        val deployFastLoad = project.tasks.register("deployFastLoad", DeployFastLoad::class.java) { task ->
            task.group = "install"

            task.getAdbExecutable().convention(extension.getAdbExecutable())
            task.getOutputDir().convention(extension.getOutputDir())
            task.getBundleBaseName().convention(extension.getBundleBaseName())
            task.getDeployLocation().convention(extension.getDeployLocation())

            task.dependsOn(assembleFastLoad)
        }

        val konfigPushFiles = project.tasks.register("konfigUpdate", KonfigPushFiles::class.java) { task ->
            task.group = "install"

            task.getAdbExecutable().convention(extension.getAdbExecutable())
            task.getOutputDir().convention(extension.getOutputDir())
            task.getBundleBaseName().convention(extension.getBundleBaseName())
            task.getDeployLocation().convention(extension.getDeployLocation())
        }

        val konfigPullFiles = project.tasks.register("konfigPull", KonfigPullFiles::class.java) { task ->
            task.group = "install"

            task.getAdbExecutable().convention(extension.getAdbExecutable())
            task.getOutputDir().convention(extension.getOutputDir())
            task.getBundleBaseName().convention(extension.getBundleBaseName())
            task.getDeployLocation().convention(extension.getDeployLocation())
        }

        val reloadFastLoad = project.tasks.register("reloadFastLoad", ReloadFastLoad::class.java) { task ->
            task.group = "install"

            task.getAdbExecutable().convention(extension.getAdbExecutable())

            task.dependsOn(deployFastLoad)
        }
    }
}

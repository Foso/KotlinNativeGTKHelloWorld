plugins {
    kotlin("multiplatform")  version "1.3.60"
}

repositories {
    mavenCentral()
}


val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

kotlin {

    // Determine host preset.
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    // Create a target for the host platform.
    val hostTarget = when {
        hostOs == "Mac OS X" -> macosX64("gtk")
        hostOs == "Linux" -> linuxX64("gtk")
        isMingwX64 -> mingwX64("gtk")
        else -> throw GradleException("Host OS '$hostOs' is not supported in Kotlin/Native $project.")
    }

    hostTarget.apply {
        binaries {
            executable {
                entryPoint = "sample.gtk.main"
                if (isMingwX64) {
                    linkerOpts("-L${mingwPath.resolve("lib")}")
                    runTask?.environment("PATH" to mingwPath.resolve("bin"))
                }
            }
        }
        compilations["main"].cinterops {



            val gtk3 by creating {
                when (preset) {
                   presets["linuxX64"] -> {
                        listOf("/opt/local/include", "/usr/include", "/usr/local/include").forEach {
                            includeDirs(
                                "$it/atk-1.0",
                                "$it/gdk-pixbuf-2.0",
                                "$it/cairo",
                                "$it/harfbuzz",
                                "$it/pango-1.0",
                                "$it/gtk-3.0",
                                "$it/glib-2.0"
                            )
                        }

                        includeDirs(
                            "/opt/local/lib/glib-2.0/include",
                            "/usr/lib/x86_64-linux-gnu/glib-2.0/include",
                            "/usr/local/lib/glib-2.0/include"
                        )
                    }

                    presets["macosX64"] -> {
                        listOf("/opt/local/include", "/usr/include", "/usr/local/include").forEach {
                            includeDirs(
                                "$it/atk-1.0",
                                "$it/gdk-pixbuf-2.0",
                                "$it/cairo",
                                "$it/harfbuzz",                                
                                "$it/pango-1.0",
                                "$it/gtk-3.0",
                                "$it/glib-2.0"
                            )
                        }

                        includeDirs(
                            "/opt/local/lib/glib-2.0/include",
                            "/usr/lib/x86_64-linux-gnu/glib-2.0/include",
                            "/usr/local/lib/glib-2.0/include"
                        )
                    }
                    presets["mingwX64"] -> {
                        listOf(
                            "include/atk-1.0",
                            "include/gdk-pixbuf-2.0",
                            "include/cairo",
                            "include/pango-1.0",
                            "include/gtk-3.0",
                            "include/glib-2.0",
                            "lib/glib-2.0/include"
                        ).forEach {
                            includeDirs(mingwPath.resolve(it))
                        }
                    }
                }
            }

            val libcurl by creating {
                when (preset) {
                    presets["macosX64"] -> includeDirs.headerFilterOnly("/opt/local/include", "/usr/local/include")
                    presets["linuxX64"] -> includeDirs.headerFilterOnly("/usr/include", "/usr/include/x86_64-linux-gnu")
                    presets["mingwX64"] -> includeDirs.headerFilterOnly(mingwPath.resolve("include"))
                }
            }

            val interop by creating{}



        }
    }
}
/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package sample.gtk

import kotlinx.cinterop.*
import gtk3.*
import interop.myFun
import interop.supply_fun
import libcurl.*



fun gtkMain(args: Array<String>): Int {
    val app = gtk_application_new("org.gtk.example", G_APPLICATION_FLAGS_NONE)!!
    g_signal_connect(app, "activate", staticCFunction {app: CPointer<GtkApplication>?, user_data: gpointer? ->FirstWindow(app, user_data).activate() })
    val status = memScoped {
        g_application_run(app.reinterpret(),
                args.size, args.map { it.cstr.ptr }.toCValues())
    }
    g_object_unref(app)
    return status
}

fun main(args: Array<String>) {
    println(myFun(4))
    gtkMain(args)
    val useMe = supply_fun()

}

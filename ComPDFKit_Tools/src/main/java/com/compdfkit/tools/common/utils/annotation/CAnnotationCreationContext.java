/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 *
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.utils.annotation;

import java.util.function.Supplier;

/**
 * Marks annotations and widgets created by API calls so tools UI can avoid treating them
 * as user-created items.
 */
public final class CAnnotationCreationContext {

    private static final ThreadLocal<Integer> PROGRAMMATIC_DEPTH =
            ThreadLocal.withInitial(() -> 0);

    private CAnnotationCreationContext() {
    }

    public static void runProgrammatic(Runnable action) {
        PROGRAMMATIC_DEPTH.set(PROGRAMMATIC_DEPTH.get() + 1);
        try {
            action.run();
        } finally {
            exitProgrammatic();
        }
    }

    public static <T> T callProgrammatic(Supplier<T> action) {
        PROGRAMMATIC_DEPTH.set(PROGRAMMATIC_DEPTH.get() + 1);
        try {
            return action.get();
        } finally {
            exitProgrammatic();
        }
    }

    public static boolean isProgrammaticCreation() {
        return PROGRAMMATIC_DEPTH.get() > 0;
    }

    private static void exitProgrammatic() {
        int nextDepth = PROGRAMMATIC_DEPTH.get() - 1;
        if (nextDepth <= 0) {
            PROGRAMMATIC_DEPTH.remove();
        } else {
            PROGRAMMATIC_DEPTH.set(nextDepth);
        }
    }
}

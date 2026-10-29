/**
 * Copyright © 2014-2026 PDF Technologies, Inc. All Rights Reserved.
 * <p>
 * THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 * AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE ComPDFKit LICENSE AGREEMENT.
 * UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 * This notice may not be removed from this file.
 */

package com.compdfkit.tools.common.pdf.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.compdfkit.tools.common.pdf.CPDFConfigurationUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CreationConfigTest {

    @Test
    public void defaultsArePreservedWhenCreationConfigIsMissing() {
        CPDFConfiguration configuration = CPDFConfigurationUtils.fromJson("{}");

        assertTrue(configuration.creationConfig.continuousCreation);
        assertTrue(configuration.creationConfig.autoSelectCreatedAnnotation);
    }

    @Test
    public void oneFieldKeepsTheOtherDefault() {
        CPDFConfiguration configuration = CPDFConfigurationUtils.fromJson(
                "{\"creationConfig\":{\"continuousCreation\":false}}"
        );

        assertFalse(configuration.creationConfig.continuousCreation);
        assertTrue(configuration.creationConfig.autoSelectCreatedAnnotation);
    }

    @Test
    public void fullConfigurationReadsBothFields() {
        CPDFConfiguration configuration = CPDFConfigurationUtils.fromJson(
                "{\"creationConfig\":{"
                        + "\"continuousCreation\":false,"
                        + "\"autoSelectCreatedAnnotation\":false}}"
        );

        assertFalse(configuration.creationConfig.continuousCreation);
        assertFalse(configuration.creationConfig.autoSelectCreatedAnnotation);
    }
}

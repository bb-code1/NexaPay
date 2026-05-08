package com.nexapay.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureBoundaryTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.nexapay");
    }

    @Test
    @DisplayName("AI tools should not directly access JPA repositories (must call Services)")
    void aiToolsShouldNotAccessRepositoriesDirectly() {
        noClasses()
                .that().resideInAPackage("..ai.tools..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .check(importedClasses);
    }
}

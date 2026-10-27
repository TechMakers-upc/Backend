package com.techmakers.fixcore;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTests {

    @Test
    void domainShouldNotDependOnFrameworksOrOuterLayers() {
        var classes = new ClassFileImporter()
                .importPackages("com.techmakers.fixcore");

        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "..application..",
                        "..infrastructure..",
                        "..interfaces.."
                )
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    void applicationShouldNotDependOnInfrastructureOrInterfaces() {
        var classes = new ClassFileImporter()
                .importPackages("com.techmakers.fixcore");

        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "..infrastructure..",
                        "..interfaces.."
                )
                .allowEmptyShould(true)
                .check(classes);
    }
}
package com.stark.springbootarchunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.stark.springbootarchunit")
public class AppLayerTest {

    static final String IMPORT_PACKAGE = "com.stark.springbootarchunit";


    @ArchTest
    static final ArchRule repository_must_not_throw_SQLException =
            noMethods().that().areDeclaredInClassesThat().haveNameMatching(".*Repository")
                    .should().declareThrowableOfType(SQLException.class);

    @ArchTest
    static final ArchRule repository_must_reside_in_a_dao_package =
            classes().that().haveNameMatching(".*Repository").should().resideInAPackage("..repository..")
                    .as("Repo's should reside in a package '..repository..'");

    @ArchTest
    static final ArchRule no_cycles_by_method_calls_between_slices =
            slices().matching("..(springbootarchunit).(*)..").namingSlices("$2 of $1").should().beFreeOfCycles();

    @ArchTest
    private final ArchRule no_field_injection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    public static final ArchRule repoRule = classes()
            .that().resideInAPackage(IMPORT_PACKAGE+".repository..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(IMPORT_PACKAGE+".service..", IMPORT_PACKAGE+".repository..");

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(IMPORT_PACKAGE);

        noClasses()
                .that().resideInAnyPackage("com.stark.springbootarchunit.service..")
                .or().resideInAnyPackage("com.stark.springbootarchunit.repository..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.stark.springbootarchunit.controller..")
                .because("Services and repositories should not depend on web layer")
                .check(importedClasses);
    }


    @Test
    void shouldNotUseJunit4Classes() {
        JavaClasses classes = new ClassFileImporter().importPackages(IMPORT_PACKAGE);

        noClasses()
                .should().accessClassesThat().resideInAnyPackage("org.junit")
                .because("Tests should use Junit5 instead of Junit4")
                .check(classes);

        noMethods().should().beAnnotatedWith("org.junit.Test")
                .orShould().beAnnotatedWith("org.junit.Ignore")
                .because("Tests should use Junit5 instead of Junit4")
                .check(classes);
    }

    @Test
    void appShouldBeFreeOfCycles(){
        JavaClasses classes = new ClassFileImporter().importPackages(IMPORT_PACKAGE);
        slices().matching(IMPORT_PACKAGE+".(*)..").should().beFreeOfCycles().check(classes);
    }

    @Test
    void repositoryShouldBeCalledFromService(){
        JavaClasses classes = new ClassFileImporter().importPackages(IMPORT_PACKAGE);
        Architectures.LayeredArchitecture arch = layeredArchitecture()
                // Define layers
                .layer("Presentation").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Persistence").definedBy("..repository..")
                // Add constraints
                .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Presentation")
                .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service");
        arch.check(classes);
    }

    @Test
    public void onlyValidRestMethodsShouldBeAllowed(){
        JavaClasses classes = new ClassFileImporter().importPackages(IMPORT_PACKAGE);
        ArchRule rule = ArchRuleDefinition.methods()
                .that().arePublic()
                .and().areDeclaredInClassesThat().resideInAPackage(IMPORT_PACKAGE + ".controller..")
                .and().areDeclaredInClassesThat().haveSimpleNameEndingWith("Controller")
                .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                .should().beAnnotatedWith(GetMapping.class)
                .orShould().beAnnotatedWith(PostMapping.class);
        rule.check(classes);
    }

}

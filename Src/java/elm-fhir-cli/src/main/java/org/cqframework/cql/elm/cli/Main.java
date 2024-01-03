package org.cqframework.cql.elm.cli;

import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.FileVisitResult.CONTINUE;

import org.cqframework.cql.elm.requirements.fhir.*;
import ca.uhn.fhir.context.FhirContext;
import org.checkerframework.checker.index.qual.Positive;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.elm.r1.*;
import ca.uhn.fhir.parser.IParser;
// import org.hl7.fhir.r5.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;

public class Main {


    // ./gradlew :elm-fhir-cli:run --args="/Users/dczulada/Desktop/Code/USCDI/SampleMeasures/CMS2" 
    @SuppressWarnings({ "unchecked", "rawtypes"})
    public static void main(String[] args) throws IOException {
        //File file = new File(args[0]);
        //String cqlPath = args[0] + "/cql";
        List<String> cmsids = Collections.EMPTY_LIST;
        Collections.addAll(cmsids = new ArrayList<String>(), "2", "22","50","56","68","69","71","72","74","75","90","104","108","122","124","129","130","131","133","135","136","138","142","143","144","145","149","153","154","157","159","165","177","190","249","314","347","349","506","529","645","646","771","816","819","826","844","951","986","1028","1056","1074","1157","1188","1206");
        //144, 145, 190, 645, 646, 1028, 1056
        //Collections.addAll(cmsids = new ArrayList<String>(),"144","145","190","645","646","1028","1056");
        //Collections.addAll(cmsids = new ArrayList<String>(),"145");
        for (String cmsid : cmsids){
        System.out.println(cmsid);
        File file = new File("/Users/dczulada/Desktop/Code/USCDI/SampleMeasures/CMS" + cmsid);
        String cqlPath = "/Users/dczulada/Desktop/Code/USCDI/SampleMeasures/CMS" + cmsid + "/cql";
        
        String measureName = "";
        String measureShortName = "";
        String measureVersion = "";
        String measureCqlFilePath = "";
        Path path = file.toPath();
        org.hl7.fhir.r5.model.Bundle originalBundle = null;

        Map<String, org.hl7.fhir.r5.model.Bundle> inOutMap = new HashMap<>();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toFile().getName().endsWith(".json") || file.toFile().getName().endsWith(".JSON")) {
                    InputStream targetStream = new FileInputStream(file.toFile());                    
                    FhirContext ctx = FhirContext.forR5();
                    IParser parser = ctx.newJsonParser();
                    org.hl7.fhir.r5.model.BaseResource res = (org.hl7.fhir.r5.model.BaseResource)parser.parseResource(targetStream);

                    if (res instanceof org.hl7.fhir.r5.model.Bundle){
                        inOutMap.put("Bundle", (org.hl7.fhir.r5.model.Bundle)res);
                    }
                }
                return CONTINUE;
            }
        });

        List<String> libraryFilePaths = new ArrayList<String>();
        for (org.hl7.fhir.r5.model.Bundle.BundleEntryComponent bec : inOutMap.get("Bundle").getEntry()){
            if (bec.getResource().getResourceType().name().equals("Measure")){
                org.hl7.fhir.r5.model.Measure mes = (org.hl7.fhir.r5.model.Measure)bec.getResource();
                measureName = mes.getName();
                for (org.hl7.fhir.r5.model.Identifier mesId : mes.getIdentifier()){
                    if (mesId.getSystem().equals("https://madie.cms.gov/measure/shortName")){
                        measureShortName = mesId.getValue();
                    }
                }
                measureVersion = mes.getVersion();
                measureCqlFilePath = cqlPath + "/" + measureName + "-" + measureVersion + ".cql";
            }
            if (bec.getResource().getResourceType().name().equals("Library")){
                org.hl7.fhir.r5.model.Library mes = (org.hl7.fhir.r5.model.Library)bec.getResource();
                var libraryName = mes.getName();
                var libraryVersion = mes.getVersion();
                libraryFilePaths.add(cqlPath + "/" + libraryName + "-" + libraryVersion + ".cql");
            }
        }
        //measureCqlFilePath = "/Users/dczulada/Desktop/Code/USCDI/SampleMeasures/What/DataRequirementHelper1-0.1.000.cql";
        File measureCqlFile = new File(measureCqlFilePath);

        CqlCompilerOptions cqlTranslatorOptions = CqlCompilerOptions.defaultOptions();
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        LibraryManager manager = setupLibraryManager(cqlTranslatorOptions, measureCqlFile.getParent());

        var compiler = new CqlCompiler(null, manager);
        var lib = compiler.run(measureCqlFile);
        manager.getCompiledLibraries().put(lib.getIdentifier(), compiler.getCompiledLibrary());
        CompiledLibrary library = compiler.getCompiledLibrary();

        Set<String> expressions = new HashSet<String>();
        for (ExpressionDef expression : library.getLibrary().getStatements().getDef()) {
            expressions.add(expression.getName());
        }
        //expressions.add("Initial Population");

        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(manager, library, cqlTranslatorOptions, expressions, true, true);
        for (org.hl7.fhir.r5.model.Bundle.BundleEntryComponent bec : inOutMap.get("Bundle").getEntry()){
            if (bec.getResource().getResourceType().name().equals("Library")){
                org.hl7.fhir.r5.model.Library ogLib = (org.hl7.fhir.r5.model.Library)bec.getResource();
                if (ogLib.getName().equals(measureName)){
                    ogLib.setDataRequirement(moduleDefinitionLibrary.getDataRequirement());
                }
            }
            if (bec.getResource().getResourceType().name().equals("Measure")){
                org.hl7.fhir.r5.model.Measure mes = (org.hl7.fhir.r5.model.Measure)bec.getResource();
                for (org.hl7.fhir.r5.model.Resource contained : mes.getContained()){
                    if (contained.getId().equals("#effective-data-requirements")){
                        org.hl7.fhir.r5.model.Library containedLibrary = (org.hl7.fhir.r5.model.Library)contained;
                        containedLibrary.setDataRequirement(moduleDefinitionLibrary.getDataRequirement());
                    }
                }
            }
        }
        FileWriter bundleWriter = new FileWriter(file.getPath() + "/" + measureShortName + "-v" + measureVersion + "-" + measureName + "_rebundled" + ".json");
        PrintWriter bundlePrintWriter = new PrintWriter(bundleWriter);
        FhirContext ctx = FhirContext.forR5();
        IParser parser = ctx.newJsonParser();
        String serializedBundle = parser.encodeResourceToString(inOutMap.get("Bundle"));
        bundlePrintWriter.println(serializedBundle);
        bundlePrintWriter.close();
        }
    }


    private static LibraryManager setupLibraryManager(CqlCompilerOptions options, String relativePath) {
        var modelManager = new ModelManager();
        var libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new DefaultLibrarySourceProvider(Paths.get(relativePath)));
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());

        return libraryManager;
    }
}

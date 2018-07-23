package org.apache.chemistry.opencmis.isbanutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.definitions.MutablePropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AbstractTypeDefinition;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.DocumentTypeDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.FolderTypeDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDecimalDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerDefinitionImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringDefinitionImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.fileshare.FileShareTypeManager;
import org.apache.chemistry.opencmis.prodoc.SesionProDoc;

import prodoc.Attribute;
import prodoc.PDDocs;
import prodoc.PDException;
import prodoc.PDFolders;
import prodoc.PDObjDefs;
import prodoc.Record;

public class ObjectTypeUtil {

    public static final int tINTEGER = 0;
    public static final int tFLOAT = 1;
    public static final int tSTRING = 2;
    public static final int tDATE = 3;
    public static final int tBOOLEAN = 4;
    public static final int tTIMESTAMP = 5;
    public static final int tTHES = 6;

    /**
     * Método para crear un Object Type de OPD en CMIS
     * 
     * @throws PDException
     */
    public static ArrayList<Object> crearObjectType(CallContext context, SesionProDoc sesion, String nombreObjectType)
            throws PDException {

        ArrayList<Object> result = new ArrayList<Object>();

        FileShareTypeManager fileShRep = new FileShareTypeManager();

        PDObjDefs objOPD = new PDObjDefs(sesion.getMainSession());
        String tipoObj;

        try {
            // Leer ObjectType OPD
            objOPD.Load(nombreObjectType);

            // Si existe el ObjectType en OPD
            if (objOPD.getName() != null) {

                Record recObjOPD = objOPD.getRecord();

                String objParentName = recObjOPD.getAttr("Parent").getValue().toString();

                // Tipo Doc
                if (objParentName.equals("PD_DOCS")) {

                    tipoObj = "cmis:document";

                    DocumentTypeDefinitionImpl typeDef = (DocumentTypeDefinitionImpl) fileShRep
                            .getTypeDefinition(context, tipoObj);

                    // Crear Object Type
                    // --> Datos "cabecera" CMIS
                    createPropertyCab(typeDef, recObjOPD, tipoObj);

                    PDDocs docAux = new PDDocs(sesion.getMainSession(), nombreObjectType);
                    Record recAux = docAux.getRecSum();

                    recAux.initList();
                    Attribute attr = recAux.nextAttr();

                    Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

                    while (attr != null) {

                        String nombreAttr = attr.getName().toString();

                        switch (nombreAttr) {

                        case "ACL":
                            break;

                        case "DocDate":
                            break;

                        case "DocType":
                            break;

                        case "LockedBy":
                            break;

                        case "MimeType":
                            break;

                        case "Name":
                            break;

                        case "PDAutor":
                            break;

                        case "PDDate":
                            break;

                        case "PDId":
                            break;

                        case "ParentId":
                            break;

                        case "Title":
                            break;

                        case "Version":
                            break;

                        // Resto de atributos
                        default:
                            createOtherProperty(propertyDefinitions, recAux, nombreAttr);
                            break;
                        }

                        attr = recAux.nextAttr();
                    }

                    // Insertamos las propiedades al objeto CMIS
                    typeDef.setPropertyDefinitions(propertyDefinitions);

                    result.add(typeDef);

                } else { // Tipo Folder

                    tipoObj = "cmis:folder";

                    FolderTypeDefinitionImpl typeDef = (FolderTypeDefinitionImpl) fileShRep.getTypeDefinition(context,
                            tipoObj);

                    // Crear Object Type
                    // Datos "cabecera" CMIS
                    createPropertyCab(typeDef, recObjOPD, tipoObj);

                    PDFolders folAux = new PDFolders(sesion.getMainSession(), nombreObjectType);
                    Record recAux = folAux.getRecSum();

                    recAux.initList();
                    Attribute attr = recAux.nextAttr();

                    Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

                    while (attr != null) {

                        String nombreAttr = attr.getName().toString();

                        switch (nombreAttr) {

                        case "ACL":
                            break;

                        case "FolderType":
                            break;

                        case "PDAutor":
                            break;

                        case "PDDate":
                            break;

                        case "PDId":
                            break;

                        case "ParentId":
                            break;

                        case "Title":
                            break;

                        // Resto de atributos
                        default:
                            createOtherProperty(propertyDefinitions, recAux, nombreAttr);
                            break;
                        }

                        attr = recAux.nextAttr();
                    }

                    // Insertamos las propiedades al objeto CMIS
                    typeDef.setPropertyDefinitions(propertyDefinitions);

                    result.add(typeDef);
                }

            }

        } catch (PDException e) {
            throw e;
        }

        return result;
    }

    /**
     * 
     * @param properties
     * @param recOPD
     * @param nombreAttr
     */
    private static void createOtherProperty(Map<String, PropertyDefinition<?>> properties, Record recOPD,
            String nombreAttr) {

        Attribute attr = recOPD.getAttr(nombreAttr);
        int tipoAttr = attr.getType();
        boolean multivaluado = attr.isMultivalued();

        switch (tipoAttr) {

        case tINTEGER:
            PropertyIntegerDefinitionImpl propInt = new PropertyIntegerDefinitionImpl();

            createProperty(propInt, PropertyType.INTEGER, nombreAttr, attr.getDescription(), multivaluado, true, false,
                    false, false, Updatability.READWRITE);

            properties.put(nombreAttr, propInt);

            break;

        case tFLOAT:
            PropertyDecimalDefinitionImpl propFloat = new PropertyDecimalDefinitionImpl();

            createProperty(propFloat, PropertyType.DECIMAL, nombreAttr, attr.getDescription(), multivaluado, true,
                    false, false, false, Updatability.READWRITE);

            properties.put(nombreAttr, propFloat);

            break;

        case tSTRING:

            PropertyStringDefinitionImpl propString = new PropertyStringDefinitionImpl();

            createProperty(propString, PropertyType.STRING, nombreAttr, attr.getDescription(), multivaluado, true,
                    false, false, false, Updatability.READWRITE);

            properties.put(nombreAttr, propString);

            break;

        case tDATE:

            PropertyDateTimeDefinitionImpl propDate = new PropertyDateTimeDefinitionImpl();

            createProperty(propDate, PropertyType.DATETIME, nombreAttr, attr.getDescription(), multivaluado, true,
                    false, false, false, Updatability.READWRITE);

            properties.put(nombreAttr, propDate);

            break;

        case tBOOLEAN:

            PropertyBooleanDefinitionImpl propBool = new PropertyBooleanDefinitionImpl();

            createProperty(propBool, PropertyType.BOOLEAN, nombreAttr, attr.getDescription(), multivaluado, true, false,
                    false, false, Updatability.READWRITE);

            properties.put(nombreAttr, propBool);

            break;

        case tTIMESTAMP:

            PropertyDateTimeDefinitionImpl propTime = new PropertyDateTimeDefinitionImpl();

            createProperty(propTime, PropertyType.DATETIME, nombreAttr, attr.getDescription(), multivaluado, true,
                    false, false, false, Updatability.READWRITE);

            properties.put(nombreAttr, propTime);

            break;

        case tTHES:
            // TODO : Hacer tratamiento
            break;

        default:
            // TODO : Hacer tratamiento
            break;
        }
    }

    /**
     * 
     * @param prop
     * @param propertyType
     * @param nombre
     * @param descripcion
     * @param multivaluado
     * @param isQueryable
     * @param isOrderable
     * @param isRequired
     * @param isInherited
     * @param updatability
     */
    private static void createProperty(MutablePropertyDefinition<?> prop, PropertyType propertyType, String nombre,
            String descripcion, boolean multivaluado, boolean isQueryable, boolean isOrderable, boolean isRequired,
            boolean isInherited, Updatability updatability) {

        prop.setDisplayName(nombre);
        prop.setDescription(descripcion);
        prop.setLocalName(nombre);
        prop.setId(nombre);
        prop.setPropertyType(propertyType);

        if (!multivaluado) {
            prop.setCardinality(Cardinality.SINGLE);
        } else {
            prop.setCardinality(Cardinality.MULTI);
        }

        prop.setIsQueryable(isQueryable);
        prop.setIsOrderable(isOrderable);
        prop.setIsRequired(isRequired);
        prop.setIsInherited(isInherited);
        prop.setUpdatability(updatability);
    }

    /**
     * 
     * @param typeDef
     * @param recObjOPD
     * @param tipoObj
     */
    private static void createPropertyCab(AbstractTypeDefinition typeDef, Record recObjOPD, String tipoObj) {

        typeDef.setId(recObjOPD.getAttr("Name").getValue().toString());
        typeDef.setLocalName(recObjOPD.getAttr("Name").getValue().toString());
        // <cmis:localNamespace>http://chemistry.apache.org/opencmis/demo/</cmis:localNamespace>
        typeDef.setParentTypeId(tipoObj);
        typeDef.setDisplayName(recObjOPD.getAttr("Name").getValue().toString());
        typeDef.setQueryName(tipoObj);
        typeDef.setDescription(recObjOPD.getAttr("Description").getValue().toString());
        BaseTypeId baseT = BaseTypeId.fromValue(tipoObj);
        typeDef.setBaseTypeId(baseT);
        typeDef.setIsCreatable(true);
        typeDef.setIsFileable(true);
        typeDef.setIsQueryable(true);
        typeDef.setIsFulltextIndexed(true);
        typeDef.setIsIncludedInSupertypeQuery(true);
        typeDef.setIsControllablePolicy(true);
        typeDef.setIsControllableAcl(true);

        if (tipoObj.equals("cmis:document")) {
            ((DocumentTypeDefinitionImpl) typeDef).setIsVersionable(false);
        }

        // <cmis:contentStreamAllowed>required</cmis:contentStreamAllowed>
    }
}

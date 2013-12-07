package org.datanucleus.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jdo.JDOEnhancer;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.DiscriminatorMetadata;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.Indexed;
import javax.jdo.metadata.InheritanceMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;
import javax.jdo.metadata.VersionMetadata;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.asm.ClassWriter;
import org.datanucleus.asm.FieldVisitor;
import org.datanucleus.asm.Label;
import org.datanucleus.asm.MethodVisitor;
import org.datanucleus.asm.Opcodes;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.datanucleus.store.schema.SchemaTool;
import org.datanucleus.util.NucleusLogger;

import junit.framework.TestCase;

/**
 * Tests that perform dynamic class generation, enhancement, and SchemaTool.
 */
public class DynamicEnhanceSchemaToolTest extends TestCase
{
    public void testEnhance()
    throws Exception
    {
        // Create an in-memory class
        String className = "test.Client";
        NucleusLogger.PERSISTENCE.info(">> Creating class in-memory");
        byte[] classBytes = createClass(className);

        // Add it to a CustomClassLoader
        DynamicEnhanceSchemaToolClassLoader workCL = new DynamicEnhanceSchemaToolClassLoader(Thread.currentThread().getContextClassLoader());
        workCL.defineClass("test.Client", classBytes);

        // Write the class to disk (debugging)
        NucleusLogger.PERSISTENCE.info(">> Writing in-memory class to target/generated/Client.class");
        File file = new File("target/generated");
        if (!file.exists())
        {
            file.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream("target/generated/Client.class");
        fos.write(classBytes);
        fos.close();

        // Create an enhancer (JDO, ASM)
        JDOEnhancer enhancer = JDOHelper.getEnhancer();
        enhancer.setClassLoader(workCL);

        // Create MetaData for the in-memory class and register it with the enhancer
        JDOMetadata jdomd = enhancer.newMetadata();
        createMetadata(jdomd);
        enhancer.registerMetadata(jdomd);
        enhancer.addClass(className, classBytes);

        // Enhance the in-memory bytes and obtain the enhanced bytes
        NucleusLogger.PERSISTENCE.info(">> Enhancing test.Client inmemory bytes");
        enhancer.enhance();
        byte[] enhancedBytes = enhancer.getEnhancedBytes(className);

        // Write the enhanced class to disk (debugging)
        NucleusLogger.PERSISTENCE.info(">> Writing enhanced in-memory class to target/enhancedClient.class");
        file = new File("target/enhanced");
        if (!file.exists())
        {
            file.mkdirs();
        }
        fos = new FileOutputStream("target/enhanced/Client.class");
        fos.write(enhancedBytes);
        fos.close();

        // Create our runtime class loader, and load the enhanced class into it
        DynamicEnhanceSchemaToolClassLoader runtimeCL = new DynamicEnhanceSchemaToolClassLoader(Thread.currentThread().getContextClassLoader());
        runtimeCL.defineClass(className, enhancedBytes);

        // SchemaTool
        NucleusLogger.PERSISTENCE.info(">> Schema creation for dynamic type");
        schemaCreate(runtimeCL);

        // Persist an object of the new type
        NucleusLogger.PERSISTENCE.info(">> Persisting an object of dynamic type");
        persist(runtimeCL);
    }

    private static byte[] createClass(String className)
    throws Exception 
    {
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;
        FieldVisitor fv;

        String classNameASM = className.replace('.', '/');
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNameASM, null, 
            "java/lang/Object", new String[]{});

        fv = cw.visitField(Opcodes.ACC_PRIVATE, "name", "Ljava/lang/String;", null, null);
        fv.visitEnd();

        // Default Constructor
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(Opcodes.RETURN);

            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + classNameASM + ";", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        // String getName()
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, classNameASM, "name", "Ljava/lang/String;");
            mv.visitInsn(Opcodes.ARETURN);

            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + classNameASM + ";", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        // void setName(String)
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setName", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitFieldInsn(Opcodes.PUTFIELD, classNameASM, "name", "Ljava/lang/String;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(Opcodes.RETURN);

            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "L" + classNameASM + ";", null, l0, l2, 0);
            mv.visitLocalVariable("s", "Ljava/lang/String;", null, l0, l2, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        // Object getProperty(String)
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getProperty", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitVarInsn(Opcodes.ASTORE, 2);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("name");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            Label l2 = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, l2);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, classNameASM, "name", "Ljava/lang/String;");
            mv.visitVarInsn(Opcodes.ASTORE, 2);
            mv.visitLabel(l2);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitInsn(Opcodes.ARETURN);

            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLocalVariable("this", "L" + classNameASM + ";", null, l0, l4, 0);
            mv.visitLocalVariable("propertyName", "Ljava/lang/String;", null, l0, l4, 1);
            mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l1, l4, 2);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }

        // void setProperty(String, Object)
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setProperty", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("name");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            mv.visitFieldInsn(Opcodes.PUTFIELD, classNameASM, "name", "Ljava/lang/String;");
            mv.visitLabel(l1);
            mv.visitInsn(Opcodes.RETURN);

            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable("this", "L" + classNameASM + ";", null, l0, l3, 0);
            mv.visitLocalVariable("propertyName", "Ljava/lang/String;", null, l0, l3, 1);
            mv.visitLocalVariable("value", "Ljava/lang/Object;", null, l0, l3, 2);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }

        return cw.toByteArray();
    }

    private static void createMetadata(JDOMetadata filemd)
    {
        PackageMetadata pmd = filemd.newPackageMetadata("test");
        ClassMetadata cmd = pmd.newClassMetadata("Client");
        cmd.setTable("CLIENT").setDetachable(true).setIdentityType(javax.jdo.annotations.IdentityType.DATASTORE);
        cmd.setPersistenceModifier(javax.jdo.metadata.ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        FieldMetadata fmd = cmd.newFieldMetadata("name");
        fmd.setNullValue(javax.jdo.annotations.NullValue.DEFAULT).setColumn("name").setIndexed(true).setUnique(true);

        InheritanceMetadata inhmd = cmd.newInheritanceMetadata();
        inhmd.setStrategy(javax.jdo.annotations.InheritanceStrategy.NEW_TABLE);
        DiscriminatorMetadata dmd = inhmd.newDiscriminatorMetadata();
        dmd.setColumn("disc").setValue("Client").setStrategy(javax.jdo.annotations.DiscriminatorStrategy.VALUE_MAP).setIndexed(Indexed.TRUE);

        VersionMetadata vermd = cmd.newVersionMetadata();
        vermd.setStrategy(javax.jdo.annotations.VersionStrategy.VERSION_NUMBER).setColumn("version").setIndexed(Indexed.TRUE);
    }

    public void persist(DynamicEnhanceSchemaToolClassLoader runtimeCL)
    throws Exception
    {
        // Persist
        Map props = getPropertiesForDatastore(runtimeCL);
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
        ClassLoaderResolver clr = ((JDOPersistenceManagerFactory)pmf).getNucleusContext().getClassLoaderResolver(runtimeCL);
        JDOMetadata filemd = pmf.newMetadata();
        createMetadata(filemd);
        NucleusLogger.PERSISTENCE.info(">> registering metadata");
        NucleusLogger.PERSISTENCE.info(filemd);
        pmf.registerMetadata(filemd);
        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx = pm.currentTransaction();
        {
            tx.begin();
            Class clazz = clr.classForName("test.Client");
            Object o = clazz.newInstance();
            pm.makePersistent(o);
            tx.commit();
        }
        pmf.close();
    }

    protected Map getPropertiesForDatastore(DynamicEnhanceSchemaToolClassLoader runtimeCL)
    {
        Map props = new HashMap();
        Properties properties = TestHelper.getPropertiesForDatastore(1);
        props.putAll(properties);
        props.put("javax.jdo.PersistenceManagerFactoryClass","org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("datanucleus.autoCreateSchema","true");
        props.put("datanucleus.autoCreateTables","true");
        props.put("datanucleus.autoCreateColumns","true");
        props.put("datanucleus.primaryClassLoader",runtimeCL);
        return props;
    }

    public void schemaCreate(DynamicEnhanceSchemaToolClassLoader runtimeCL)
    throws Exception 
    {
        Map props = getPropertiesForDatastore(runtimeCL);
        JDOPersistenceManagerFactory pmf = (JDOPersistenceManagerFactory) JDOHelper.getPersistenceManagerFactory(props);
        JDOMetadata filemd = pmf.newMetadata();
        createMetadata(filemd);
        pmf.registerMetadata(filemd);

        Set<String> classNames = new HashSet();
        classNames.add("test.Client");

        NucleusContext nucCtx = pmf.getNucleusContext();
        StoreManager storeMgr = nucCtx.getStoreManager();
        if (!(storeMgr instanceof SchemaAwareStoreManager))
        {
            // Can't create schema with this datastore
            return;
        }

        try
        {
            SchemaTool schematool = new SchemaTool();
            schematool.setDdlFile("target/schema.ddl");
            schematool.setCompleteDdl(true);
            SchemaAwareStoreManager schemaStoreMgr = (SchemaAwareStoreManager) nucCtx.getStoreManager();
            schematool.createSchema(schemaStoreMgr, classNames);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        pmf.close();
    }
}

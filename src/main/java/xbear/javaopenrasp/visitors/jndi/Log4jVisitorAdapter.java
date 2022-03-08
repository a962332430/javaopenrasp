package xbear.javaopenrasp.visitors.jndi;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author guo.chen
 * @date: 2022-03-08
 */
public class Log4jVisitorAdapter extends AdviceAdapter {
    public Log4jVisitorAdapter(MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM5, mv, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Already in jndi filter");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        mv.visitTypeInsn(NEW, "xbear/javaopenrasp/filters/jndi/Log4JFilter");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "xbear/javaopenrasp/filters/jndi/Log4JFilter", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 3);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "xbear/javaopenrasp/filters/jndi/Log4JFilter", "filter", "(Ljava/lang/Object;)Z", false);

        Label l92 = new Label();
        mv.visitJumpInsn(IFNE, l92);
        mv.visitTypeInsn(NEW, "javax/naming/NamingException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("====> Error looking up JNDI resource because of security protect");
        mv.visitMethodInsn(INVOKESPECIAL, "javax/naming/NamingException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l92);

    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
    }
}


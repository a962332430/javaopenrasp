package xbear.javaopenrasp.visitors.jndi;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;

/**
 * @author guo.chen
 * @date: 2022-03-08
 */
public class Log4jVisitor extends ClassVisitor {

    private static final Set<String> methodName = new HashSet<String>() {{
        add("lookup");
    }};

    public String className;

    public Log4jVisitor(ClassVisitor cv, String className) {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (methodName.contains(name)) {
            mv = new Log4jVisitorAdapter(mv, access, name, desc);
        }
        return mv;
    }

}

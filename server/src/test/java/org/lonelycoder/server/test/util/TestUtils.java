package org.lonelycoder.server.test.util;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * @author : lihaoquan
 */
public class TestUtils {

    public static String outputOracle(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static String outputSqlServer(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static String outputOracle(SQLStatement... stmtList) {
        return outputOracle(Arrays.asList(stmtList));
    }

    public static String outputSqlServer(SQLStatement... stmtList) {
        return outputSqlServer(Arrays.asList(stmtList));
    }

    public static String output(SQLStatement... stmtList) {
        return output(Arrays.asList(stmtList));
    }

    public static String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static long getYoungGC() {
        try {
            // java.lang:type=GarbageCollector,name=G1 Young Generation
            // java.lang:type=GarbageCollector,name=G1 Old Generation
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName;
            if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ParNew"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=ParNew");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=Copy"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=Copy");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=G1 Young Generation"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=G1 Young Generation");
            } else {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS Scavenge");
            }

            return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
        } catch (Exception e) {
            throw new RuntimeException("error");
        }
    }

    public static long getYoungGCTime() {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName;
            if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ParNew"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=ParNew");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=Copy"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=Copy");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=G1 Young Generation"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=G1 Young Generation");
            } else {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS Scavenge");
            }

            return (Long) mbeanServer.getAttribute(objectName, "CollectionTime");
        } catch (Exception e) {
            throw new RuntimeException("error", e);
        }
    }

    public static long getFullGC() {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName;

            if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=G1 Old Generation"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=G1 Old Generation");
            } else {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS MarkSweep");
            }

            return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
        } catch (Exception e) {
            throw new RuntimeException("error");
        }
    }
}

package org.lonelycoder.core.database.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;

import java.util.List;

/**
 * @author : lihaoquan
 */
public class CommonSQLParser {

    /**
     * 解析语句
     * @param sql
     * @return
     */
    public OracleSchemaStatVisitor parseSQL(String sql) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);
        return visitor;
    }
}

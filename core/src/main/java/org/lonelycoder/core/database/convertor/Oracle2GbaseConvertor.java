package org.lonelycoder.core.database.convertor;

import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.lonelycoder.core.database.parser.CommonSQLParser;

import java.util.List;

/**
 * @author : lihaoquan
 */
public class Oracle2GbaseConvertor implements SQLConvertor {

    @Override
    public String convert(String sql) {

        String handle_sql = "";

        CommonSQLParser commonSQLParser = new CommonSQLParser();
        OracleSchemaStatVisitor visitor = commonSQLParser.parseSQL(sql);
        List<TableStat.Condition> conditionList = visitor.getConditions();

        //... ....

        return handle_sql;
    }
}

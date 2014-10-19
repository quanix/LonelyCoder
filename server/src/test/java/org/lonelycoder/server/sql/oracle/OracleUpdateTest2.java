/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lonelycoder.server.sql.oracle;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;
import org.lonelycoder.server.test.OracleTest;

import java.util.List;

public class OracleUpdateTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "UPDATE wrh$_tempfile tfh " //
                     + "    SET (snap_id, filename, tsname) =" //
                     + "    (SELECT :lah_snap_id, tf.name name, ts.name tsname"
                     + //
                     "      FROM v$tempfile tf, ts$ ts"
                     + //
                     "      WHERE tf.ts# = ts.ts# AND tfh.file# = tf.file# AND tfh.creation_change# = tf.creation_change#"
                     + //
                     "  )" + //
                     "WHERE (file#, creation_change#) IN        (" + //
                     "          SELECT tf.tfnum, to_number(tf.tfcrc_scn) creation_change#           " + //
                     "          FROM x$kcctf tf           " + //
                     "          WHERE tf.tfdup != 0)    AND dbid    = :dbid    AND snap_id < :snap_id"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("wrh$_tempfile")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("x$kcctf")));

        Assert.assertEquals(4, visitor.getTables().size());
        Assert.assertEquals(18, visitor.getColumns().size());

//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("table1", "column")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("table2", "expr")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("table2", "column")));
    }

}
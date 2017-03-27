package org.opencloudb.parser.druid;

import org.junit.Test;
import org.opencloudb.manager.parser.druid.MycatManageStatementParser;
import org.opencloudb.manager.parser.druid.statement.MycatCheckTbStructConsistencyStatement;
import org.opencloudb.manager.parser.druid.statement.MycatChecksumTableStatement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.parser.ParserException;

import junit.framework.Assert;

public class MycatManageStatementParserTest {
	
	@Test(expected = ParserException.class)
	public void testParseCheckTbStructConsistencyStatement() {
		String sql = "check table structure consistency for TESTDB.*, SCHEMA1.TB1";
		MycatManageStatementParser parser = new MycatManageStatementParser(sql);
		SQLStatement stmt = parser.parseStatement();
		Assert.assertEquals(MycatCheckTbStructConsistencyStatement.class, stmt.getClass());
		Assert.assertEquals(2, ((MycatCheckTbStructConsistencyStatement)stmt).getNameList().size());
		Assert.assertEquals("TESTDB.*", ((MycatCheckTbStructConsistencyStatement)stmt).getNameList().get(0).toString());
		Assert.assertEquals("SCHEMA1.TB1", ((MycatCheckTbStructConsistencyStatement)stmt).getNameList().get(1).toString());

		// TODO where条件解析
		sql = "check table structure consistency for TESTDB where table_name = ?";
		parser = new MycatManageStatementParser(sql);
		stmt = parser.parseStatement();
		MycatCheckTbStructConsistencyStatement _stmt = (MycatCheckTbStructConsistencyStatement) stmt;
		Assert.assertEquals("table_name = ?", _stmt.getWhere().toString());
		
		// 解析抛错
		sql = "check table structure consistency";
		parser = new MycatManageStatementParser(sql);
		stmt = parser.parseStatement();
	}
	
	@Test(expected = ParserException.class)
	public void testParseChecksumTableStatement() {
		String sql = "checksum table TESTDB.hotnews";
		MycatManageStatementParser parser = new MycatManageStatementParser(sql);
		SQLStatement stmt = parser.parseStatement();
		Assert.assertEquals(MycatChecksumTableStatement.class, stmt.getClass());
		MycatChecksumTableStatement _stmt = (MycatChecksumTableStatement)stmt;
		Assert.assertEquals("TESTDB.hotnews", _stmt.getTableName().toString());
		SQLPropertyExpr tableName = (SQLPropertyExpr) _stmt.getTableName();
		Assert.assertEquals("TESTDB", tableName.getOwner().toString());
		Assert.assertEquals("hotnews", tableName.getSimpleName());
	
		sql = "checksum TESTDB.hotnews";
		parser = new MycatManageStatementParser(sql);
		stmt = parser.parseStatement();
	}
	
}

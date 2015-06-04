package com.elong.pb.newdda.parser;

import com.elong.pb.newdda.parser.ast.stmt.SQLStatement;
import com.elong.pb.newdda.parser.syntax.MySQLParser;

import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyong on 15/5/27.
 * sql解析代理器(我不知道怎么做，只能一个字符一个字符的敲，资质是这样，我会继续努力)
 */
public final class SQLParserDelegate {

    private static enum SpecialIdentifier {
        ROLLBACK,
        SAVEPOINT,
        TRUNCATE
    }

    private static final Map<String, SpecialIdentifier> specialIdentifiers = new HashMap<String, SpecialIdentifier>();

    static {
        specialIdentifiers.put("TRUNCATE", SpecialIdentifier.TRUNCATE);
        specialIdentifiers.put("SAVEPOINT", SpecialIdentifier.SAVEPOINT);
        specialIdentifiers.put("ROLLBACK", SpecialIdentifier.ROLLBACK);
    }

    public static SQLStatement parse(String sql, String charset) throws SQLSyntaxErrorException {
        return parse(sql, new MySQLLexer(sql), charset);
    }

    public static SQLStatement parse(String sql) throws SQLSyntaxErrorException {
        return parse(sql, MySQLParser.DEFAULT_CHARSET);
    }

    public static SQLStatement parse(String sql, MySQLLexer lexer, String charset) throws SQLSyntaxErrorException {
        try {
            SQLStatement stmt = null;
            boolean isEOF = true;
            stmtSwith:
            switch (lexer.token()) {
                case KW_SELECT:
                case PUNC_LEFT_PAREN:
                    stmt = null;
                    break stmtSwith;
                default:
                    throw new SQLSyntaxErrorException("sql is not a supported statement");
            }
            if (isEOF) {
                while (lexer.token() == MySQLToken.PUNC_SEMICOLON) {
                    lexer.nextToken();
                }
                if (lexer.token() != MySQLToken.EOF) {
                    throw new SQLSyntaxErrorException("SQL syntax error!");
                }
            }
            return stmt;
        } catch (Exception e) {
            throw new SQLSyntaxErrorException(buildErrorMsg(e, lexer, sql), e);
        }
    }

    private static String buildErrorMsg(Exception e, MySQLLexer lexer, String sql) {
        StringBuilder sb = new StringBuilder(
                "You have an error in your SQL syntax; Error occurs around this fragment: ");
        final int ch = lexer.getCurrentIndex();
        int from = ch - 16;
        if (from < 0) {
            from = 0;
        }
        int to = ch + 9;
        if (to >= sql.length()) {
            to = sql.length() - 1;
        }
        String fragment = sql.substring(from, to + 1);
        sb.append('{').append(fragment).append('}').append(". Error cause: " + e.getMessage());
        return sb.toString();
    }

    public static void main(String[] args) throws SQLSyntaxErrorException {
        MySQLLexer lexer = new MySQLLexer("  select * from users where a = 1");
        System.out.println(111);
    }

}

/*
 * Copyright 1999-2012 Alibaba Group.
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
/**
 * (created at 2011-5-21)
 */
package com.elong.pb.newdda.parser.ast.stmt.dal;

import com.elong.pb.newdda.parser.ast.expression.Expression;
import com.elong.pb.newdda.parser.ast.expression.primary.Identifier;
import com.elong.pb.newdda.parser.visitor.SQLASTVisitor;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class ShowTables extends DALShowStatement {
    private final boolean full;
    private Identifier schema;
    private final String pattern;
    private final Expression where;

    public ShowTables(boolean full, Identifier schema, String pattern) {
        this.full = full;
        this.schema = schema;
        this.pattern = pattern;
        this.where = null;
    }

    public ShowTables(boolean full, Identifier schema, Expression where) {
        this.full = full;
        this.schema = schema;
        this.pattern = null;
        this.where = where;
    }

    public ShowTables(boolean full, Identifier schema) {
        this.full = full;
        this.schema = schema;
        this.pattern = null;
        this.where = null;
    }

    public boolean isFull() {
        return full;
    }

    public void setSchema(Identifier schema) {
        this.schema = schema;
    }

    public Identifier getSchema() {
        return schema;
    }

    public String getPattern() {
        return pattern;
    }

    public Expression getWhere() {
        return where;
    }

    @Override
    public void accept(SQLASTVisitor visitor) {
        visitor.visit(this);
    }
}

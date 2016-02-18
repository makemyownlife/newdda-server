package com.elong.pb.newdda.config.rule;

import java.util.Collections;
import java.util.List;

public class TableRuleConfig {
    private final String name;
    private final List<RuleConfig> rules;

    public TableRuleConfig(String name, List<RuleConfig> rules) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        this.name = name;
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("no rule is found");
        }
        this.rules = Collections.unmodifiableList(rules);
    }

    public String getName() {
        return name;
    }

    /**
     * @return unmodifiable
     */
    public List<RuleConfig> getRules() {
        return rules;
    }

}

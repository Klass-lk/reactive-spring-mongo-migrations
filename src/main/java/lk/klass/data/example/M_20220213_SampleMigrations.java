/*
 *
 *  * Copyright (C) 2022. KLASS.LK
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package lk.klass.data.example;

import lk.klass.data.MongoMigrationScript;
import lk.klass.data.annots.Migration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author danushka
 * 2022-02-13
 */
@Migration
@Component
@RequiredArgsConstructor
public class M_20220213_SampleMigrations extends MongoMigrationScript {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<?> up() {
        // sample script to move teacher attribute to a teachers list
        var subjectUpdateQuery = new Query();
        subjectUpdateQuery.addCriteria(Criteria.where("teacher").exists(true));

        return mongoTemplate.find(subjectUpdateQuery, Map.class, "teachers")
                .flatMap(this::updateTeachersAttribute)
                .collectList();
    }

    private Mono<Subject> updateTeachersAttribute(Map<String, Object> fieldValues) {
        var teacherId = fieldValues.get("teacher");
        var teachers = List.of(teacherId);

        var update = new Update();
        update.set("teachers", teachers);
        update.unset("teacher");

        var query = new Query();
        query.addCriteria(Criteria.where("_id").is(fieldValues.get("_id")));

        return mongoTemplate.findAndModify(query, update, Subject.class);
    }
}

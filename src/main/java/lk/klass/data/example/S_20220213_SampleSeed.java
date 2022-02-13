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

import lk.klass.data.MongoSeedScript;
import lk.klass.data.annots.DataSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author danushka
 * 2022-02-13
 */
@Component
@DataSeeder
@RequiredArgsConstructor
public class S_20220213_SampleSeed extends MongoSeedScript {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<?> seed() {
        // Sample data seeder script
        var subject = new Subject("abcd", List.of("1234"));
        return mongoTemplate.save(subject);
    }
}

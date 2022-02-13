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

package lk.klass.data;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lk.klass.data.annots.DataSeeder;
import lk.klass.data.annots.Migration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * @author danushka
 * 11/6/2021
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScriptRunner implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationContext applicationContext;
    private final MigrationRepository migrationRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan();
        ClassInfoList migrations = scanResult.getClassesWithAnnotation(Migration.class);
        ClassInfoList seeds = scanResult.getClassesWithAnnotation(DataSeeder.class);

        Flux.fromIterable(seeds.loadClasses())
                .sort(Comparator.comparing(Class::getName))
                .flatMap(aClass -> migrationRepository.existsById(aClass.getSimpleName())
                        .filter(e -> !e).map(c -> aClass))
                .flatMap(this::runSeedScript)
                .doOnNext(changelog -> log.info("Script {} executed successfully", changelog.getFilename()))
                .subscribe();

        Flux.fromIterable(migrations.loadClasses())
                .sort(Comparator.comparing(Class::getSimpleName))
                .flatMap(aClass -> migrationRepository.existsById(aClass.getSimpleName())
                        .filter(e -> !e).map(c -> aClass))
                .flatMap(this::runMigrationScript)
                .doOnNext(changelog -> log.info("Script {} executed successfully", changelog.getFilename()))
                .subscribe();


    }

    private Mono<Changelog> runSeedScript(Class<?> aClass) {
        MongoSeedScript bean = (MongoSeedScript) applicationContext.getBean(aClass);
        return bean.seed()
                .flatMap(result -> migrationRepository.save(new Changelog(
                        aClass.getSimpleName(),
                        aClass.getName(),
                        DateTime.now()
                )));
    }

    private Mono<Changelog> runMigrationScript(Class<?> aClass) {
        MongoMigrationScript bean = (MongoMigrationScript) applicationContext.getBean(aClass);
        return bean.up()
                .flatMap(result -> migrationRepository.save(new Changelog(
                        aClass.getSimpleName(),
                        aClass.getName(),
                        DateTime.now()
                )));
    }
}

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

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author danushka
 * 2022-02-13
 */
@Document("changelogs")
@Data
public class Changelog {
    @Id
    private final String id;
    private String filename;
    private DateTime processedTime;

    public Changelog(String id, String filename, DateTime processedTime) {
        this.id = id;
        this.filename = filename;
        this.processedTime = processedTime;
    }
}

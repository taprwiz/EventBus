/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.eventbus.indexed;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusJavaTestsIndex;
import org.greenrobot.eventbus.EventBusTestsIndex;

public class Indexed {
    static EventBus build() {
        // TODO ut: remove index of this module once all tests moved to Java module
        return EventBus.builder()
                .addIndex(new EventBusTestsIndex())
                .addIndex(new EventBusJavaTestsIndex())
                .build();
    }
}

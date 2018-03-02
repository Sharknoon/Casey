/*
 * Copyright 2018 Shark Industries.
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
package sharknoon.dualide.misc;

/**
 *
 * @author Josua Frank
 */
public enum RadioStations {
    //DUFM du-radio.com
    DUFM("http://ns511142.ip-198-27-66.net:8048/stream");
    //Hopefully more to come

    private final String url;

    private RadioStations(String url) {
        this.url = url;
    }

    /**
     * Returns the URL if this radio
     *
     * @return The URL of this radio
     */
    public String getURL() {
        return url;
    }
}

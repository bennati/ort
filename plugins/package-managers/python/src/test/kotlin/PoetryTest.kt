/*
 * Copyright (C) 2023 The ORT Project Authors (see <https://github.com/oss-review-toolkit/ort/blob/main/NOTICE>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.plugins.packagemanagers.python

import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.file.aFile
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

import org.ossreviewtoolkit.plugins.packagemanagers.python.Pip.Companion.PYPROJECT_FILENAME

class PoetryTest : WordSpec({
    "parseScopeNamesFromPyProject()" should {
        "return the 'main' scope even for a non-existing file" {
            val pyprojectFile = tempdir().resolve(PYPROJECT_FILENAME)

            pyprojectFile shouldNotBe aFile()
            parseScopeNamesFromPyproject(pyprojectFile) shouldBe setOf("main")
        }

        "parse scope names with different syntax" {
            val pyprojectFile = tempdir().resolve(PYPROJECT_FILENAME)

            pyprojectFile.writeText(
                """
                    [tool.poetry.dev-dependencies]
                    [tool.poetry.group.test.dependencies]
                """.trimIndent()
            )

            parseScopeNamesFromPyproject(pyprojectFile) shouldBe setOf("main", "dev", "test")
        }

        "not return empty scope names" {
            val pyprojectFile = tempdir().resolve(PYPROJECT_FILENAME)

            pyprojectFile.writeText(
                """
                    [tool.poetry.dependencies]
                """.trimIndent()
            )

            parseScopeNamesFromPyproject(pyprojectFile) shouldBe setOf("main")
        }
    }
})

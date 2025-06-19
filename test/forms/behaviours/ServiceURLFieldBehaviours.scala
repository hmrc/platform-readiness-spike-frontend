/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.behaviours

import play.api.data.{Form, FormError}

trait ServiceURLFieldBehaviours extends FieldBehaviours {

  def fieldWithMaxLength(form: Form[_],
                           fieldName: String,
                           maxLength: Int,
                           lengthError: FormError): Unit = {

    s"not bind strings longer than $maxLength characters" in {

      forAll(serviceURLsLongerThan(maxLength) -> "longString") {
        (string: String) =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain only lengthError
      }
    }
  }

  def fieldThatRequiresURLPrefix(form: Form[_],
                         fieldName: String,
                         prefix: String,
                         urlError: FormError): Unit = {

    s"not bind strings which don't start with the URL Prefix" in {

      forAll(stringsNotStartingWithValue(prefix) -> "nonURL") {
        (string: String) =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain (urlError)
      }
    }
  }
}

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

package forms.buildResilience

import forms.behaviours.ServiceURLFieldBehaviours
import play.api.data.FormError

class ServiceURLFormProviderSpec extends ServiceURLFieldBehaviours {

  val requiredKey = "serviceURL.error.required"
  val lengthKey = "serviceURL.error.length"
  val urlKey = "serviceURL.error.url"
  val maxLength = 100
  val prefix = "https://catalogue.tax.service.gov.uk/repositories/"

  val form = new ServiceURLFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      serviceURLsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldThatRequiresURLPrefix(
      form,
      fieldName,
      prefix = prefix,
      urlError = FormError(fieldName, urlKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}

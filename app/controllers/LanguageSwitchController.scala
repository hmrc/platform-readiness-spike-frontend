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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import play.api.i18n.Lang
import play.api.mvc.*
import uk.gov.hmrc.play.language.{LanguageController, LanguageUtils}

class LanguageSwitchController @Inject()(
                                          appConfig: FrontendAppConfig,
                                          languageUtils: LanguageUtils,
                                          cc: ControllerComponents
                                        ) extends LanguageController(languageUtils, cc) {

  override def fallbackURL: String = routes.IndexController.onPageLoad().url

  override def languageMap: Map[String, Lang] = appConfig.languageMap
}

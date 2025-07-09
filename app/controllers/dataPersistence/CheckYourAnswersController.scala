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

package controllers.dataPersistence

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import pages.dataPersistence.{UsingMongoPage, UsingObjectStorePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.dataPersistence.*
import viewmodels.govuk.summarylist.*
import views.html.dataPersistence.CheckYourAnswersView
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val mongoList =
        if (request.userAnswers.get(UsingMongoPage).getOrElse(false)) {
            Seq(
              UsingMongoSummary.row(request.userAnswers),
              ResilientRecycleMongoSummary.row(request.userAnswers),
              PublicMongoTTLSummary.row(request.userAnswers),
              FieldLevelEncryptionSummary.row(request.userAnswers),
              ProtectedMongoTTLSummary.row(request.userAnswers),
              MongoTestedWithIndexingSummary.row(request.userAnswers)
            )
        } else {
            Seq(
              UsingMongoSummary.row(request.userAnswers)
            )
        }

      val objectList =
        if (request.userAnswers.get(UsingObjectStorePage).getOrElse(false)) {
            Seq(
              UsingObjectStoreSummary.row(request.userAnswers),
              CorrectRetentionPeriodSummary.row(request.userAnswers)
            )
        } else {
            Seq(
              UsingObjectStoreSummary.row(request.userAnswers)
            )
        }

      val dataList:SummaryList = SummaryListViewModel(rows = mongoList.flatten ++ objectList.flatten)

      Ok(view(dataList))

  }
}

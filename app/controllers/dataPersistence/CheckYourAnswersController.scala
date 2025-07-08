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
      
      val resilientRecycleMongoSummaryRow  =
        if request.userAnswers.get(UsingMongoPage).getOrElse(false) then ResilientRecycleMongoSummary.row(request.userAnswers)
        else None

      val publicMongoTTLSummaryRow =
        if request.userAnswers.get(UsingMongoPage).getOrElse(false) then PublicMongoTTLSummary.row(request.userAnswers)
        else None

      val fieldLevelEncryptionSummaryRow =
        if request.userAnswers.get(UsingMongoPage).getOrElse(false) then FieldLevelEncryptionSummary.row(request.userAnswers)
        else None

      val protectedMongoTTLSummaryRow =
        if request.userAnswers.get(UsingMongoPage).getOrElse(false) then ProtectedMongoTTLSummary.row(request.userAnswers)
        else None

      val mongoTestedWithIndexingSummaryRow =
        if request.userAnswers.get(UsingMongoPage).getOrElse(false) then MongoTestedWithIndexingSummary.row(request.userAnswers)
        else None
      
      val correctRetentionPeriodSummaryRow  =
        if request.userAnswers.get(UsingObjectStorePage).getOrElse(false) then CorrectRetentionPeriodSummary.row(request.userAnswers)
        else None

      val list = SummaryListViewModel(
        rows = Seq(UsingMongoSummary.row(request.userAnswers),
          resilientRecycleMongoSummaryRow,
          publicMongoTTLSummaryRow,
          fieldLevelEncryptionSummaryRow,
          protectedMongoTTLSummaryRow,
          mongoTestedWithIndexingSummaryRow,
          UsingObjectStoreSummary.row(request.userAnswers),
          correctRetentionPeriodSummaryRow
        ).flatten
      )

      Ok(view(list))

  }
}

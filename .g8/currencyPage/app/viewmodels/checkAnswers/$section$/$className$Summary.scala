package viewmodels.checkAnswers.$section$

import config.CurrencyFormatter.currencyFormat
import controllers.$section$.routes
import models.{CheckMode, UserAnswers}
import pages.$section$.$className$Page
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object $className$Summary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get($className$Page).map {
      answer =>

        SummaryListRowViewModel(
          key     = "$className;format="decap"$.checkYourAnswersLabel",
          value   = ValueViewModel(currencyFormat(answer)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.$className$Controller.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("$className;format="decap"$.change.hidden"))
          )
        )
    }
}

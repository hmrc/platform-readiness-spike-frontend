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

package config

import models.repositories.{AssessedService, GitRepository, Tag}

object QuestionStructure {

  val BuildAndResilience: AssessmentSection = AssessmentSection(
    "build-and-resilience",
    Seq(
      QuestionPage("nonstandard-pattern"),
      QuestionPage("bobby-rules"),
      QuestionPage("http-verbs"),
      QuestionPage("deprecated-libraries"),
      QuestionPage("readme"),
      QuestionPage("appropriate-timeouts")
    )
  )

  val DataPersistence: AssessmentSection = AssessmentSection(
    "data-persistence",
    Seq(
      QuestionPage("using-mongo"),
      QuestionPage("mongo-recycle"),
      QuestionPage("public-mongo-ttl"),
      QuestionPage("field-level-encryption"),
      QuestionPage("protected-mongo-ttl"),
      QuestionPage("performance-tested"),
      QuestionPage("object-store"),
      QuestionPage("retention-period")
    )
  )

  val CommonServiceUsage: AssessmentSection = AssessmentSection(
    "common-service-usage",
    Seq(
      QuestionPage("integration"),
      QuestionPage("notify-dependent-services")
    )
  )

  val Security: AssessmentSection = AssessmentSection(
    "security",
    Seq(
      QuestionPage("frontend-auth"),
      QuestionPage("public-auth"),
      QuestionPage("protected-auth")
    )
  )

  val AdminServices: AssessmentSection = AssessmentSection(
    "admin-service",
    Seq(
      QuestionPage("no-public-route"),
      QuestionPage("stride-or-vpn"),
      QuestionPage("stride-or-internal-auth"),
      QuestionPage("access-to-production")
    )
  )

  val assessmentSections: Seq[AssessmentSection] = Seq(
    BuildAndResilience,
    DataPersistence,
    CommonServiceUsage,
    Security,
    AdminServices
  )

  def sections(service: AssessedService): Seq[AssessmentSection] = {
    if (service.isAdminService) assessmentSections
    else assessmentSections.filter(_ != AdminServices)
  }

  val sectionsMap: Map[String, Seq[QuestionPage]] = assessmentSections.map(s => s.name -> s.questionPages).toMap()

}

case class AssessmentSection(
  name: String,
  questionPages: Seq[QuestionPage]
)

case class QuestionPage(
  name: String
)
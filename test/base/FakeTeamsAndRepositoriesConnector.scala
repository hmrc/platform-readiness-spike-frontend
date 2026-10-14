/*
 * Copyright 2026 HM Revenue & Customs
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

package base

import models.repositories.{GitRepository, Organisation, RepoType, ServiceType, Tag}
import uk.gov.hmrc.http.HeaderCarrier
import connectors.TeamsAndRepositoriesConnector

import java.time.Instant
import scala.concurrent.Future

class FakeTeamsAndRepositoriesConnector extends TeamsAndRepositoriesConnector {
  def allRepositories(using HeaderCarrier): Future[Seq[GitRepository]] = Future.successful(Seq(FakeTeamsAndRepositoriesConnector.testRepository))
  def getRepository(name: String)(using HeaderCarrier): Future[Option[GitRepository]] = Future.successful(Some(FakeTeamsAndRepositoriesConnector.testRepository))
}

object FakeTeamsAndRepositoriesConnector {
  val testRepository: GitRepository = generateTestRepository("some-frontend", ServiceType.Frontend, None)
  def generateTestRepository(name: String, serviceType: ServiceType, tags: Option[Set[Tag]]): GitRepository = GitRepository(
    name = name,
    organisation = Some(Organisation.Mdtp),
    description = "abc",
    url = "https://github.com/hmrc/catalogue-frontend",
    createdDate = Instant.now,
    lastActiveDate = Instant.now,
    endOfLifeDate = Some(Instant.now),
    isPrivate = false,
    repoType = RepoType.Service,
    serviceType = Some(serviceType),
    tags = tags,
    digitalServiceName = None,
    owningTeams = Seq.empty,
    language = None,
    isArchived = false,
    defaultBranch = "main",
    isDeprecated = false,
    teamNames = Seq.empty,
    prototypeName = None,
    prototypeAutoPublish = None
  )
}

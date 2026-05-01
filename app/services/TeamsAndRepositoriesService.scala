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

package services

import models.repositories.GitRepository
import models.repositories.ServiceType.{Backend, Frontend}
import models.repositories.{GitRepository, Organisation, RepoType, ServiceType, Tag}
import java.time.Instant

import com.google.inject.{ImplementedBy, Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace

//Added in temporarily to replace the use of the connector as in Staging we have no access to TeamsAndRepositories
@Singleton
class TeamsAndRepositoriesServiceImpl @Inject()()(using ExecutionContext)
    extends TeamsAndRepositoriesService {

  def allRepositories: Future[Seq[GitRepository]] = {
    Future.successful(TeamsAndRepositoriesService.repos.values.toSeq)
  }


  def getRepository(name: String): Future[Option[GitRepository]] = {
    Future.successful(TeamsAndRepositoriesService.repos.get(name))
  }

}

object TeamsAndRepositoriesService {
  val repos: Map[String, GitRepository] = Map(
    "pertax-frontend" -> testRepo("pertax-frontend", Frontend, None),
    "pertax" -> testRepo("pertax", Backend, None),
    "digital-disclosure-service" -> testRepo("digital-disclosure-service", Backend, None),
    "dms-submission-admin-frontend" -> testRepo("dms-submission-admin-frontend", Frontend, Some(Set(Tag.AdminFrontend)))
  )

  private def testRepo(name: String, serviceType: ServiceType, tags: Option[Set[Tag]]) = GitRepository(
    name = name,
    organisation = Some(Organisation.Mdtp),
    description = "abc",
    url = s"https://github.com/hmrc/$name",
    createdDate = Instant.now,
    lastActiveDate = Instant.now,
    endOfLifeDate = Some(Instant.now),
    isPrivate = false,
    repoType = RepoType.Service,
    serviceType = Some(serviceType),
    tags = tags,
    digitalServiceName = None,
    owningTeams  = Seq.empty,
    language = None,
    isArchived = false,
    defaultBranch = "main",
    isDeprecated = false,
    teamNames = Seq.empty,
    prototypeName = None,
    prototypeAutoPublish = None
  )
}

@ImplementedBy(classOf[TeamsAndRepositoriesServiceImpl])
trait TeamsAndRepositoriesService {
  def allRepositories: Future[Seq[GitRepository]]
  def getRepository(name: String): Future[Option[GitRepository]]
}
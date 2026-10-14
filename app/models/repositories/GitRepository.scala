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

package models.repositories

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import utils.{FormFormat, FromString, FromStringEnum, Parser}

import java.time.Instant
import FromStringEnum.*

given Parser[RepoType] = Parser.parser(RepoType.values)

enum Organisation(
                   override val asString: String
                 ) extends FromString
  derives Reads, Writes:
  case Mdtp                   extends Organisation("mdtp")
  case External(name: String) extends Organisation(name)

given Parser[Organisation] =
  (s: String) =>
    s.toLowerCase match
      case Organisation.Mdtp.asString => Right(Organisation.Mdtp)
      case o                          => Right(Organisation.External(o))

enum RepoType(
               override val asString: String
             ) extends FromString
  derives Ordering, Reads, Writes, FormFormat:
  case Service   extends RepoType("Service"  )
  case Library   extends RepoType("Library"  )
  case Prototype extends RepoType("Prototype")
  case Test      extends RepoType("Test"     )
  case Other     extends RepoType("Other"    )

given Parser[ServiceType] = Parser.parser(ServiceType.values)

enum ServiceType(
                  override val asString: String,
                  val displayString    : String
                ) extends FromString
  derives Ordering, Reads, Writes, FormFormat:
  case Frontend extends ServiceType(asString = "frontend", displayString = "Frontend")
  case Backend  extends ServiceType(asString = "backend" , displayString = "Backend")

given Parser[Tag] = Parser.parser(Tag.values)

enum Tag(
          override val asString: String,
          val displayString    : String
        ) extends FromString
  derives Ordering, Reads, Writes:
  case AdminFrontend    extends Tag(asString = "admin"             , displayString = "Admin Frontend"    )
  case Api              extends Tag(asString = "api"               , displayString = "API"               )
  case External         extends Tag(asString = "external"          , displayString = "External"          )
  case BuiltOffPlatform extends Tag(asString = "built-off-platform", displayString = "Built Off Platform")
  case Maven            extends Tag(asString = "maven"             , displayString = "Maven"             )
  case Stub             extends Tag(asString = "stub"              , displayString = "Stub"              )
end Tag

case class BranchProtection(
                             requiresApprovingReviews: Boolean,
                             dismissesStaleReviews: Boolean,
                             requiresCommitSignatures: Boolean
                           ):
  def isProtected: Boolean =
    requiresApprovingReviews && dismissesStaleReviews && requiresCommitSignatures

object BranchProtection:
  implicit val writes: Writes[BranchProtection] = Json.writes[BranchProtection]
  val reads: Reads[BranchProtection] =
    ((__ \ "requiresApprovingReviews").read[Boolean]
      ~ (__ \ "dismissesStaleReviews").read[Boolean]
      ~ (__ \ "requiresCommitSignatures").read[Boolean]
      )(apply)

case class GitRepository(
                          name                : String,
                          organisation        : Option[Organisation],
                          description         : String,
                          url                 : String,
                          createdDate         : Instant,
                          lastActiveDate      : Instant,
                          endOfLifeDate       : Option[Instant]          = None,
                          isPrivate           : Boolean                  = false,
                          repoType            : RepoType                 = RepoType.Other,
                          serviceType         : Option[ServiceType]      = None,
                          tags                : Option[Set[Tag]]         = None,
                          digitalServiceName  : Option[String]           = None,
                          owningTeams         : Seq[String]              = Seq.empty,
                          language            : Option[String],
                          isArchived          : Boolean,
                          defaultBranch       : String,
                          branchProtection    : Option[BranchProtection] = None,
                          isDeprecated        : Boolean                  = false,
                          teamNames           : Seq[String]              = Seq.empty,
                          prototypeName       : Option[String]           = None,
                          prototypeAutoPublish: Option[Boolean]          = None,
                        ):
    def isAdminService: Boolean = tags.getOrElse(Set()).contains(Tag.AdminFrontend)
end GitRepository

object GitRepository:
  val writes: Writes[GitRepository] = Json.writes[GitRepository]
  val reads: Reads[GitRepository] =
    ( (__ \ "name"                ).read[String]
      ~ (__ \ "organisation"        ).readNullable[Organisation]
      ~ (__ \ "description"         ).read[String]
      ~ (__ \ "url"                 ).read[String]
      ~ (__ \ "createdDate"         ).read[Instant]
      ~ (__ \ "lastActiveDate"      ).read[Instant]
      ~ (__ \ "endOfLifeDate"       ).readNullable[Instant]
      ~ (__ \ "isPrivate"           ).readWithDefault[Boolean](false)
      ~ (__ \ "repoType"            ).read[RepoType]
      ~ (__ \ "serviceType"         ).readNullable[ServiceType]
      ~ (__ \ "tags"                ).readNullable[Set[Tag]]
      ~ (__ \ "digitalServiceName"  ).readNullable[String]
      ~ (__ \ "owningTeams"         ).readWithDefault[Seq[String]](Seq.empty)
      ~ (__ \ "language"            ).readNullable[String]
      ~ (__ \ "isArchived"          ).readWithDefault[Boolean](false)
      ~ (__ \ "defaultBranch"       ).read[String]
      ~ (__ \ "branchProtection"    ).readNullable(BranchProtection.reads)
      ~ (__ \ "isDeprecated"        ).readWithDefault[Boolean](false)
      ~ (__ \ "teamNames"           ).readWithDefault[Seq[String]](Seq.empty)
      ~ (__ \ "prototypeName"       ).readNullable[String]
      ~ (__ \ "prototypeAutoPublish").readNullable[Boolean]
      )(apply)


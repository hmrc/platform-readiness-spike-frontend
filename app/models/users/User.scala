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

///*
// * Copyright 2026 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package models.users
//
//case class Role(asString: String):
//  def displayName: String =
//    asString.split("_").map(_.capitalize).mkString(" ")
//
//  def isUser: Boolean =
//    asString == "user"
//
//object Role:
//  val reads: Reads[Role] =
//    summon[Reads[String]].map(Role.apply)
//
//case class User(
//                 displayName   : Option[String],
//                 familyName    : String,
//                 givenName     : Option[String],
//                 organisation  : Option[String],
//                 primaryEmail  : String,
//                 username      : String,
//                 githubUsername: Option[String],
//                 phoneNumber   : Option[String],
//                 role          : Role,
//                 teamNames     : Seq[String],
//                 isDeleted     : Boolean,
//                 isNonHuman    : Boolean
//               )
//
//object User:
//  val reads: Reads[User] =
//    ( ( __ \ "displayName"   ).readNullable[String]
//      ~ ( __ \ "familyName"    ).read[String]
//      ~ ( __ \ "givenName"     ).readNullable[String]
//      ~ ( __ \ "organisation"  ).readNullable[String]
//      ~ ( __ \ "primaryEmail"  ).read[String]
//      ~ ( __ \ "username"      ).read[UserName]
//      ~ ( __ \ "githubUsername").readNullable[String]
//      ~ ( __ \ "phoneNumber"   ).readNullable[String]
//      ~ ( __ \ "role"          ).read[Role](Role.reads)
//      ~ ( __ \ "teamNames"     ).read[Seq[String]]
//      ~ ( __ \ "isDeleted"     ).read[Boolean]
//      ~ ( __ \ "isNonHuman"    ).read[Boolean]
//      )(User.apply)
//
//
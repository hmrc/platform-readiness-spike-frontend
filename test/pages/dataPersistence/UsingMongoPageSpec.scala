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

package pages.dataPersistence

import base.SpecBase
import models.UserAnswers

import scala.util.Try

class UsingMongoPageSpec extends SpecBase {

  "UsingMongoPage" - {

    "Must Clear Mongo Pages when answer is changed from YES to NO" in {
      //step 1 data set up
      //set up user answers which contains each mongo page
      val userAnswersWithMongoPages = for {
        withMongo <- UserAnswers("id").set(UsingMongoPage, true)
        withResilient <- withMongo.set(ResilientRecycleMongoPage, false)
        withPublic <- withResilient.set(PublicMongoTTLPage, false)
        withField <- withPublic.set(FieldLevelEncryptionPage, false)
        withProtected <- withField.set(ProtectedMongoTTLPage, false)
        withTestedIndex <- withProtected.set(MongoTestedWithIndexingPage, false)
      } yield withTestedIndex


      //step 2 call code in test /trigger test
      //
      val actual = userAnswersWithMongoPages.flatMap(_.set(UsingMongoPage, false))

      //step 3 check outcome is expected
      //
      actual.foreach(_.get(ResilientRecycleMongoPage) mustBe None)
      actual.foreach(_.get(PublicMongoTTLPage) mustBe None)
      actual.foreach(_.get(FieldLevelEncryptionPage) mustBe None)
      actual.foreach(_.get(ProtectedMongoTTLPage) mustBe None)
      actual.foreach(_.get(MongoTestedWithIndexingPage) mustBe None)

    }
    "Must Not Clear Mongo Pages when Answer is changed from NO to YES" in {
      //step 1 data set up
      //set up user answers which contains each mongo page
      val userAnswersWithMongoPages = for {
        withMongo <- UserAnswers("id").set(UsingMongoPage, false)
        withResilient <- withMongo.set(ResilientRecycleMongoPage, false)
        withPublic <- withResilient.set(PublicMongoTTLPage, false)
        withField <- withPublic.set(FieldLevelEncryptionPage, false)
        withProtected <- withField.set(ProtectedMongoTTLPage, false)
        withTestedIndex <- withProtected.set(MongoTestedWithIndexingPage, false)
      } yield withTestedIndex


      //step 2 call code in test /trigger test
      //
      val actual = userAnswersWithMongoPages.flatMap(_.set(UsingMongoPage, true))

      //step 3 check outcome is expected
      //
      actual.foreach(_.get(ResilientRecycleMongoPage) mustBe Some(false))
      actual.foreach(_.get(PublicMongoTTLPage) mustBe Some(false))
      actual.foreach(_.get(FieldLevelEncryptionPage) mustBe Some(false))
      actual.foreach(_.get(ProtectedMongoTTLPage) mustBe Some(false))
      actual.foreach(_.get(MongoTestedWithIndexingPage) mustBe Some(false))
    }
    "Must Clear Mongo Pages when Answer is unchanged and Answer is NO" in {
      //step 1 data set up
      //set up user answers which contains each mongo page
      val userAnswersWithMongoPages = for {
        withMongo <- UserAnswers("id").set(UsingMongoPage, false)
        withResilient <- withMongo.set(ResilientRecycleMongoPage, false)
        withPublic <- withResilient.set(PublicMongoTTLPage, false)
        withField <- withPublic.set(FieldLevelEncryptionPage, false)
        withProtected <- withField.set(ProtectedMongoTTLPage, false)
        withTestedIndex <- withProtected.set(MongoTestedWithIndexingPage, false)
      } yield withTestedIndex


      //step 2 call code in test /trigger test
      //
      val actual = userAnswersWithMongoPages.flatMap(_.set(UsingMongoPage, false))

      //step 3 check outcome is expected
      //
      actual.foreach(_.get(ResilientRecycleMongoPage) mustBe None)
      actual.foreach(_.get(PublicMongoTTLPage) mustBe None)
      actual.foreach(_.get(FieldLevelEncryptionPage) mustBe None)
      actual.foreach(_.get(ProtectedMongoTTLPage) mustBe None)
      actual.foreach(_.get(MongoTestedWithIndexingPage) mustBe None)
    }
    "Must not do anything when Answer is unchanged and Answer is YES" in {
      //step 1 data set up
      //set up user answers which contains each mongo page
      val userAnswersWithMongoPages = for {
        withMongo <- UserAnswers("id").set(UsingMongoPage, true)
        withResilient <- withMongo.set(ResilientRecycleMongoPage, false)
        withPublic <- withResilient.set(PublicMongoTTLPage, false)
        withField <- withPublic.set(FieldLevelEncryptionPage, false)
        withProtected <- withField.set(ProtectedMongoTTLPage, false)
        withTestedIndex <- withProtected.set(MongoTestedWithIndexingPage, false)
      } yield withTestedIndex


      //step 2 call code in test /trigger test
      //
      val actual = userAnswersWithMongoPages.flatMap(_.set(UsingMongoPage, true))

      //step 3 check outcome is expected
      //
      actual.foreach(_.get(ResilientRecycleMongoPage) mustBe Some(false))
      actual.foreach(_.get(PublicMongoTTLPage) mustBe Some(false))
      actual.foreach(_.get(FieldLevelEncryptionPage) mustBe Some(false))
      actual.foreach(_.get(ProtectedMongoTTLPage) mustBe Some(false))
      actual.foreach(_.get(MongoTestedWithIndexingPage) mustBe Some(false))
    }
  }

}

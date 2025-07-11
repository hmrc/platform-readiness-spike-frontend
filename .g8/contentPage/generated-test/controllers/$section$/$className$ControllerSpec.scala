package controllers.$section$

import controllers.routes
import controllers.$section$.routes as $section$Routes

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.$section$.$className$View

class $className$ControllerSpec extends SpecBase {

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, $section$Routes.$className$Controller.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[$className$View]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}

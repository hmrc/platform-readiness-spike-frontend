#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/$section$.routes"

echo "" >> ../conf/$section$.routes
echo "GET        /$className;format="decap"$                        controllers.$section$.$className$Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/$section$.routes
echo "POST       /$className;format="decap"$                        controllers.$section$.$className$Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/$section$.routes

echo "GET        /change$className$                  controllers.$section$.$className$Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/$section$.routes
echo "POST       /change$className$                  controllers.$section$.$className$Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/$section$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $title$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $title$" >> ../conf/messages.en
echo "$className;format="decap"$.$option1key;format="decap"$ = $option1msg$" >> ../conf/messages.en
echo "$className;format="decap"$.$option2key;format="decap"$ = $option2msg$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Select $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.en

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$: Arbitrary[$className$] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf($className$.values)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Migration $className;format="snake"$ completed"

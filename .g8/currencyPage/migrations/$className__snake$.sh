#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/$section$.routes"

echo "" >> ../conf/$section$.routes
echo "GET        /$className;format="decap"$                  controllers.$section$.$className$Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/$section$.routes
echo "POST       /$className;format="decap"$                  controllers.$section$.$className$Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/$section$.routes

echo "GET        /change$className$                        controllers.$section$.$className$Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/$section$.routes
echo "POST       /change$className$                        controllers.$section$.$className$Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/$section$.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.error.nonNumeric = Enter your $className;format="decap"$ using numbers and a decimal point" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Enter your $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.invalidNumeric = Enter your $className;format="decap"$ using up to two decimal places" >> ../conf/messages.en
echo "$className;format="decap"$.error.aboveMaximum = $className$ must be {0} or less" >> ../conf/messages.en
echo "$className;format="decap"$.error.belowMinimum = $className$ must be {0} or more" >> ../conf/messages.en
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.en

echo "Migration $className;format="snake"$ completed"

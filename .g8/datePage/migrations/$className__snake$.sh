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
echo "$className;format="decap"$.hint = For example, 12 11 2007." >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required.all = Enter the $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required.two = The $className;format="decap"$" must include {0} and {1} >> ../conf/messages.en
echo "$className;format="decap"$.error.required = The $className;format="decap"$ must include {0}" >> ../conf/messages.en
echo "$className;format="decap"$.error.invalid = Enter a real $className$" >> ../conf/messages.en
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.en

echo "Migration $className;format="snake"$ completed"

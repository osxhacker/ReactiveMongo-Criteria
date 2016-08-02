/*
 * Copyright 2016 Steve Vickers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Created on: Jun 2, 2013
 */
package reactivemongo.extensions.dsl.criteria

import org.scalatest._
import org.scalatest.matchers._

import reactivemongo.bson._


/**
 * The '''TypedCriteriaSpec''' type unit tests the
 * [[reactivemongo.extensions.dsl.criteria.Typed]] EDSL functionality and
 * serves both to verify fitness as well as an exemplar to how the
 * [[reactivemongo.extensions.dsl.criteria.Typed]] functionality can be used.
 *
 * @author svickers
 *
 */
class TypedCriteriaSpec
	extends FlatSpec
	with Matchers
{
	/// Class Types
	case class Nested (description : String)
	case class ExampleDocument (age : Int, name : String, nested : Nested)


	/// Class Imports
	import Typed._


	"A Typed criteria" should "support equality comparisons" in
	{
		BSONDocument.pretty (
			criteria[ExampleDocument].name === "a value"
			) shouldBe (
			BSONDocument.pretty (
				BSONDocument ("name" -> BSONString ("a value"))
				)
			);

		BSONDocument.pretty (
			criteria[ExampleDocument].name @== "another value"
			) shouldBe (
			BSONDocument.pretty (
				BSONDocument ("name" -> BSONString ("another value"))
				)
			);
	}

    /// TODO: enforce the nested type referenced in the selector
	ignore should "support nested object selectors" in
	{
		val q = criteria[ExampleDocument].nested.description =/= "something";

		BSONDocument.pretty (q) shouldBe (
			BSONDocument.pretty (
				BSONDocument (
					"nested.description" -> BSONDocument ("$ne" -> "something")
					)
				)
			);
	}
}


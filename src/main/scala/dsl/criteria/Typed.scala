/*
 * Copyright 2013 Steve Vickers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on: Jun 22, 2014
 */
package reactivemongo.extensions.dsl.criteria

import scala.language.dynamics
import scala.language.experimental.macros


/**
 * The '''Typed''' `object` provides the ability to ''lift'' an arbitrary type
 * `T` into the [[reactivemongo.extensions.dsl.criteria]] world.  Each property
 * is represented as a [[reactivemongo.extensions.dsl.criteria.Term]].
 *
 * @author svickers
 *
 */
object Typed
{
	/// Class Types
	/**
	 * The '''PropertyAccess''' type exists for syntactic convenience when
	 * the `criteria` method is used.  The tandem allow for constructs such
	 * as:
	 *
	 * {{{
	 * import Typed._
	 *
	 * val typeCheckedQuery = criteria[SomeType] (_.first) < 10 && (
	 *    criteria[SomeType] (_.second) >= 20.0 ||
	 *    criteria[SomeType] (_.second).in (0.0, 1.0)
	 *    );
	 * }}}
	 *
	 * @author svickers
	 *
	 */
	final class PropertyAccess[ParentT <: AnyRef]
	{
		def apply[T] (statement : ParentT => T) : Term[T] =
			macro TypedMacros.createTerm[ParentT, T];
	}


	/**
	 * The criteria method produces a type which enforces the existence of
	 * property names within ''T''.
	 */
	def criteria[T <: AnyRef] : PropertyAccess[T] = new PropertyAccess[T];
}


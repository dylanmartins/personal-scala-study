package actors

import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object ActorsIntro {

  val simpleActorBehavior: Behavior[String] = Behaviors.receive { (context, message) =>
    // do something
    println(s"Received message: $message")

    // new behavior for the NEXT message
    Behaviors.same
  }

  def demoSimpleActor(): Unit = {
    // part 2 instantiate the actor
    val actorSystem = ActorSystem(Person(), "FirstActorSystem")

    // part 3 communicate with the actor
    // Note: message should be the same type as simpleActorBehavior expects Behavior[String]
//    actorSystem ! "Hello, Actor!" // async
    actorSystem ! "sad" // async

    // part 4 shut down the actor system
    Thread.sleep(1000) // wait for the actor to process the message
    actorSystem.terminate()
  }

  // "Refactor"
  object simpleActorRefactored {
    def apply(): Behavior[String] = Behaviors.receiveMessage { (message: String) =>
      // do something
      println(s"Received message: $message")

      // new behavior for the NEXT message
      Behaviors.same
    }
  }

  object simpleActorV2 {
    def apply(): Behavior[String] = Behaviors.receive { (context, message) =>
      // do something
      context.log.info(s"[simpleActorV2] Received message: $message")
      Behaviors.same
    }
  }

  object Person {
    def happy(): Behavior[String] = Behaviors.receive { (context, message) =>
      message match {
        case "sad" =>
          context.log.info(s"I'm a happy person, but I've received '$message'. Let's be sad.")
          sad()
        case _ =>
          context.log.info(s"I've received '$message'. That's great!'")
          Behaviors.same
      }
    }

    def sad(): Behavior[String] = Behaviors.receive { (context, message) =>
      message match {
        case "happy" =>
          context.log.info(s"I'm a sad person, but I've received '$message'. Let's be happy.")
          happy()
        case _ =>
          context.log.info(s"I've received '$message'. That's sucks!'")
          Behaviors.same
      }
    }

    def apply(): Behavior[String] = {
      // You can choose the initial behavior here
      happy()
    }
  }

  def testPerson(): Unit = {
    val actorSystem = ActorSystem(Person(), "PersonActorSystem")

    // Send messages to the Person actor
    actorSystem ! "I love blue"
    actorSystem ! "sad"
    actorSystem ! "I also love red"
    actorSystem ! "happy"
    actorSystem ! "happy"

    Thread.sleep(1000) // wait for the actor to process the messages
    // Shut down the actor system
    actorSystem.terminate()
  }

  object WeirdActor {
    def apply(): Behavior[Any] = Behaviors.receive { (context, message) =>
      message match {
        case msg: String =>
          context.log.info(s"[WeirdActor] Received String message: $msg")
        case number: Int =>
          context.log.info(s"[WeirdActor] Received Int message: $number")
        case _ =>
          context.log.info(s"[WeirdActor] Received unknown message type: ${message.getClass.getSimpleName}")
      }
      Behaviors.same
    }
  }

  // Add wrapper type and type hierarchy
  object BetterActor {
    trait Message
    case class StringMessage(msg: String) extends Message
    case class IntMessage(number: Int) extends Message

    def apply(): Behavior[Message] = Behaviors.receive { (context, message) =>
      message match {
        case msg: StringMessage =>
          context.log.info(s"[WeirdActor] Received String message: $msg")
        case number: IntMessage =>
          context.log.info(s"[WeirdActor] Received Int message: $number")
      }
      Behaviors.same
    }
  }

  def demoWeirdActor(): Unit = {
    import actors.ActorsIntro.BetterActor.*

    val actorSystem = ActorSystem(BetterActor(), "WeirdActorSystem")

    // Send different types of messages to the WeirdActor
    actorSystem ! StringMessage("Hello, Weird Actor!")
    actorSystem ! IntMessage(42)
//    actorSystem ! 3.14 // doesn't work, as the actor expects Message type

    Thread.sleep(1000) // wait for the actor to process the messages
    actorSystem.terminate()
  }

  def main(args: Array[String]): Unit = {
//    demoSimpleActor()
//    testPerson()
    demoWeirdActor()
  }
}

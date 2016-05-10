package controllers

import play.api.mvc.{Action, Controller}
import views.html.main

class ApplicationController extends Controller {
  def index() = Action {
    Ok(main.render())
  }
}

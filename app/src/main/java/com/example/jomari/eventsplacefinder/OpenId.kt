package com.example.jomari.eventsplacefinder

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_openid.*
import java.util.*


const val RC_SIGN_IN = 123
lateinit var mGoogleSignInClient : GoogleSignInClient

val user = FirebaseAuth.getInstance()

class OpenId : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    lateinit var mLoginbtn: Button
    lateinit var mLoginEmail: EditText
    lateinit var mLoginPassword: EditText
    lateinit var mProgressbar: ProgressDialog
    lateinit var auth: FirebaseAuth
    var compEmail : String = ""
    var compPwd : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Twitter.initialize(this)
        setContentView(R.layout.activity_openid)

        mLoginbtn = findViewById(R.id.submit_button)

        mLoginEmail = findViewById(R.id.email_tv)
        mLoginPassword = findViewById(R.id.password_tv)
        mProgressbar = ProgressDialog(this)

        mLoginbtn.setOnClickListener {
            val email = mLoginEmail.text.toString().trim()
            val password = mLoginPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                mLoginEmail.error = " Enter Email"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                mLoginPassword.error = " Enter Password"
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        //GOOOOOOOOOOOOOOOOOOOOOOOOOGLE//

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientId))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.visibility = View.VISIBLE
        tv_name.visibility = View.GONE
        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            sign_in_button.visibility = View.GONE
            login_button.visibility = View.GONE
            loginButtonTwitter.visibility = View.GONE
            tv_name.text = acct.displayName
            tv_name.visibility = View.VISIBLE
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish()
            //remove other buttons
        }

        val fbAndTwitter = user.currentUser
        if(fbAndTwitter != null){
            sign_in_button.visibility = View.GONE
            login_button.visibility = View.GONE
            loginButtonTwitter.visibility = View.GONE
            tv_name.text = fbAndTwitter.displayName
            tv_name.visibility = View.VISIBLE
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish()
        }

        //FACEEEEEEEEEEEEEEEEEEEEEEEEEEBOOK

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        callbackManager = CallbackManager.Factory.create()
        auth = FirebaseAuth.getInstance()

        val EMAIL = "email"

        val loginButton = findViewById<View>(R.id.login_button) as LoginButton
        loginButton.setReadPermissions(Arrays.asList(EMAIL))

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("tag", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })


        //Twitter

        val loginButtonTwitter = findViewById<View>(R.id.loginButtonTwitter) as TwitterLoginButton
        loginButtonTwitter.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.d("tag", "twitterLogin:success$result")
                handleTwitterSession(result.data)
            }

            override fun failure(exception: TwitterException) {
                Log.w("tag", "twitterLogin:failure", exception)
                updateUI(null)
            }
        }

    }

    private fun loginUser(email: String, password: String) {
        mProgressbar.setMessage("Please wait..")
        mProgressbar.show()

        val ref = FirebaseDatabase.getInstance().getReference("companies").orderByChild("company_email").equalTo(email)

        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                compEmail = p0.value.toString()
                compPwd = p0.value.toString()
            }

        })
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task->
                if (task.isSuccessful || (compEmail==email && compPwd==password)) {
                    mProgressbar.dismiss()
                    val currentCompany = mAuth.currentUser
                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()
                    updateUI(currentCompany)
                    val startIntent = Intent(applicationContext, CompanyMessages::class.java)
                    startActivity(startIntent)
                    finish()
                }
                else{
                    password_tv.setText("")
                    Toast.makeText(this, "Invalid Account", Toast.LENGTH_LONG).show()
                    //Toast.makeText(this, "Authentication failed.${task.exception}", Toast.LENGTH_LONG).show()
                }

                mProgressbar.dismiss()
            }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }


    private fun updateUI(currentUser: FirebaseUser?) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

        //GOOGLE

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        //twitter

        loginButtonTwitter!!.onActivityResult(requestCode, resultCode, data)
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("tag", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("tag", "signInWithCredential:success")
                    textView.visibility = View.GONE
                    textView18.visibility = View.GONE
                    email_tv.visibility = View.GONE
                    password_tv.visibility = View.GONE
                    submit_button.visibility = View.GONE
                    tv_name.text = getString(R.string.logSucess)
                    login_button.visibility = View.GONE
                    sign_in_button.visibility = View.GONE
                    loginButtonTwitter.visibility = View.GONE
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()
                    updateUI(user)
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("tag", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }

    //GOOOOOOOOOOOOOOOOOOOOOOOOOOGLE

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account)
            }
            tv_name.text = account!!.displayName
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            sign_in_button.visibility = View.VISIBLE
            tv_name.text = ""
            tv_name.visibility = View.GONE

        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("tag", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("tag", "signInWithCredential:success")
                    textView.visibility = View.GONE
                    textView18.visibility = View.GONE
                    email_tv.visibility = View.GONE
                    password_tv.visibility = View.GONE
                    submit_button.visibility = View.GONE
                    tv_name.text = getString(R.string.logSucess)
                    login_button.visibility = View.GONE
                    sign_in_button.visibility = View.GONE
                    loginButtonTwitter.visibility = View.GONE
                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("tag", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }

    //twitter

    private fun handleTwitterSession(session: TwitterSession) {
        Log.d("tag", "handleTwitterSession:$session")

        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret
        )

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("tag", "signInWithCredential:success")
                    textView.visibility = View.GONE
                    textView18.visibility = View.GONE
                    email_tv.visibility = View.GONE
                    password_tv.visibility = View.GONE
                    submit_button.visibility = View.GONE
                    tv_name.text = getString(R.string.logSucess)
                    login_button.visibility = View.GONE
                    sign_in_button.visibility = View.GONE
                    loginButtonTwitter.visibility = View.GONE
                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("tag", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

}
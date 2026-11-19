package com.talent.boomchat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.User
import com.talent.boomchat.databinding.ActivityAuthBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var isSignUpMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        setupListeners()
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                isSignUpMode = tab?.position == 1
                if (isSignUpMode) {
                    binding.layoutConfirmPassword.visibility = View.VISIBLE
                    binding.btnSubmit.text = "Create Account"
                } else {
                    binding.layoutConfirmPassword.visibility = View.GONE
                    binding.btnSubmit.text = "Sign In"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Valid email is required"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            if (isSignUpMode) {
                val confirm = binding.etConfirmPassword.text.toString().trim()
                if (password != confirm) {
                    binding.etConfirmPassword.error = "Passwords do not match"
                    return@setOnClickListener
                }
                performSignUp(email, password)
            } else {
                performSignIn(email, password)
            }
        }
    }

    private fun performSignUp(email: String, pass: String) {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val result = FirebaseManager.auth.createUserWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // Send verification email
                    firebaseUser.sendEmailVerification().await()

                    // Initialize template profile in firestore
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = email,
                        displayName = "",
                        isEmailVerified = false,
                        isProfileComplete = false
                    )
                    FirebaseManager.createUserProfile(newUser)

                    Toast.makeText(this@AuthActivity, "Account created! Verification link sent to $email", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@AuthActivity, VerifyEmailActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthActivity, "Sign up failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun performSignIn(email: String, pass: String) {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val result = FirebaseManager.auth.signInWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    if (!firebaseUser.isEmailVerified) {
                        Toast.makeText(this@AuthActivity, "Please verify your email address to continue", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AuthActivity, VerifyEmailActivity::class.java))
                    } else {
                        val profile = FirebaseManager.getUserProfile(firebaseUser.uid)
                        if (profile == null || !profile.isProfileComplete) {
                            startActivity(Intent(this@AuthActivity, ProfileSetupActivity::class.java))
                        } else {
                            startActivity(Intent(this@AuthActivity, com.talent.boomchat.ui.MainActivity::class.java))
                        }
                    }
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthActivity, "Sign in failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !loading
    }
}

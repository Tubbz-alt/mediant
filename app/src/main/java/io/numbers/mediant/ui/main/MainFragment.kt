package io.numbers.mediant.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import io.numbers.mediant.BuildConfig.APPLICATION_ID
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentMainBinding
import io.numbers.mediant.ui.tab.Tab
import io.numbers.mediant.util.ActivityRequestCodes
import io.numbers.mediant.util.PermissionManager
import io.numbers.mediant.util.PermissionRequestType
import io.numbers.mediant.util.SnackbarArgs
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MainFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var tabs: List<Tab>

    @Inject
    lateinit var permissionManager: PermissionManager

    private lateinit var mainPagerAdapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[MainViewModel::class.java]
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        viewModel.showSnackbar.observe(viewLifecycleOwner, EventObserver { showSnackBar(it) })
    }

    private fun initViewPager() {
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) =
                tabs[tab.position].fragment.smoothScrollToTop()
        })
        activity?.also { mainPagerAdapter = MainPagerAdapter(tabs, it, childFragmentManager) }
            ?: run { throw RuntimeException("Illegal activity") }
        viewPager.adapter = mainPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.selectedOptionsItem.value = item.itemId
        when (item.itemId) {
            R.id.menuItemNavToSettings -> findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
            R.id.menuItemOpenCamera -> prepareCamera()
        }
        return true
    }

    private fun prepareCamera() {
        if (permissionManager.hasPermissions(PermissionRequestType.PROOFMODE)) {
            dispatchTakePhotoIntent()
        } else if (!permissionManager.askPermissions(PermissionRequestType.PROOFMODE, this)) {
            navigateToPermissionRationaleFragment(PermissionRequestType.PROOFMODE.value.rationale)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ActivityRequestCodes.CAMERA.value -> if (resultCode == Activity.RESULT_OK) viewModel.uploadPhoto()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionRequestType.PROOFMODE.value.code -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    dispatchTakePhotoIntent()
                } else navigateToPermissionRationaleFragment(PermissionRequestType.PROOFMODE.value.rationale)
            }
        }
    }

    private fun dispatchTakePhotoIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.also { fragmentActivity ->
            // Ensure that there's a camera activity to handle the intent.
            intent.resolveActivity(fragmentActivity.packageManager)?.also {
                // Create the File where the photo should go.
                val photoFile = viewModel.createPhotoFile(fragmentActivity.filesDir)
                val photoUri = FileProvider.getUriForFile(
                    fragmentActivity, "$APPLICATION_ID.provider", photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, ActivityRequestCodes.CAMERA.value)
            }
        }
    }

    private fun navigateToPermissionRationaleFragment(@StringRes rationale: Int) {
        MainFragmentDirections.actionMainFragmentToPermissionRationaleFragment(rationale)
            .also { findNavController().navigate(it) }
    }

    private fun showSnackBar(snackbarArgs: SnackbarArgs) = view?.let {
        val snackbar = Snackbar.make(it, snackbarArgs.message, snackbarArgs.duration)
        snackbar.setAction(R.string.dismiss) { snackbar.dismiss() }
        snackbar.show()
    }
}